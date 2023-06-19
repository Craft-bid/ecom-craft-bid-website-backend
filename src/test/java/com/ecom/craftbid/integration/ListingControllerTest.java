package com.ecom.craftbid.integration;

import com.ecom.craftbid.dtos.*;
import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.init.DataInitializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListingControllerTest extends BaseIntegrationTest {

    @Test
    public void testGetListingById() throws Exception {
        long id = 1;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ListingDTO responseListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);

        Listing listing = listingRepository.findById(id).orElseThrow();
        assertEquals(listing.getId(), responseListing.getId());
        assertEquals(listing.getTags().size(), responseListing.getTags().size());
    }

    @Test
    public void testSearchNoCriteria() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/search"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ListingDTO> responseListings = new ObjectMapper().readValue(responseContent, new TypeReference<>() {
        });

        List<Listing> listings = listingRepository.findAll();
        assertEquals(listings.size(), responseListings.size());
    }

    @Test
    public void testSearchByTitle() throws Exception {
        Listing listing = listingRepository.findAll().get(0);

        String title = listing.getTitle();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/search?title=" + title))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ListingDTO> responseListings = new ObjectMapper().readValue(responseContent, new TypeReference<>() {
        });

        int expectedSize = (int) listingRepository.findAll().stream().filter(l -> l.getTitle().equals(title)).count();
        assertEquals(expectedSize, responseListings.size());
        assertEquals(title, responseListings.get(0).getTitle());
    }

    @Test
    public void testCreateListing() throws Exception {
        ListingCreateRequest listingCreateRequest = new ListingCreateRequest();
        listingCreateRequest.setTitle("Test Title");
        listingCreateRequest.setDescription("Test Description");
        listingCreateRequest.setAdvertiserId(1L);
        listingCreateRequest.setEnded(false);

        String requestContent = new ObjectMapper().writeValueAsString(listingCreateRequest);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/private/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ListingDTO createdListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(createdListing);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/private/listings/" + createdListing.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testPatchListing() throws Exception {

        Listing listing = listingRepository.findAll().get(0);

        Listing updatedListing = new Listing();
        updatedListing.setTitle("Updated Title");
        updatedListing.setEnded(true);
        updatedListing.setDescription("Updated Description");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestContent = objectMapper.writeValueAsString(updatedListing);


        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/private/listings/{id}", listing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(MockMvcResultMatchers.status().isOk());


        Listing updatedListingInDb = listingRepository.findById(listing.getId()).orElseThrow();
        assertEquals(updatedListing.getTitle(), updatedListingInDb.getTitle());
        assertEquals(updatedListing.getEnded(), updatedListingInDb.getEnded());
        assertEquals(updatedListing.getDescription(), updatedListingInDb.getDescription());
    }

    @Test
    public void testPatchListingWinner() throws Exception {
        Listing listing = listingRepository.findAll().get(0);

        ListingUpdateRequest updatedListing = new ListingUpdateRequest();
        updatedListing.setWinnerId(1L);
        updatedListing.setEnded(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestContent = objectMapper.writeValueAsString(updatedListing);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/private/listings/{id}", listing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Listing updatedListingInDb = listingRepository.findById(listing.getId()).orElseThrow();
        assertEquals(1L, updatedListingInDb.getWinner().getId());
    }

    @Test
    public void testAddTagsToListingThenRemoveOne() throws Exception {
        Listing listing = listingRepository.findAll().get(0);
        long listingId = listing.getId();

        TagDTO tag1 = TagDTO.builder().name("tag1").build();
        TagDTO tag2 = TagDTO.builder().name("tag2").build();

        List<TagDTO> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);

        String json = new ObjectMapper().writeValueAsString(tags);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/private/" + listingId + "/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ListingDTO responseListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);

        assertEquals(listing.getTags().size(), responseListing.getTags().size());

        long tagIdToRemove = responseListing.getTags().iterator().next().getId();
        int tagCountBeforeDelete = responseListing.getTags().size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/private/" + listingId + "/tags/" + tagIdToRemove))
                .andExpect(MockMvcResultMatchers.status().isOk());

        result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/" + listingId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        responseContent = result.getResponse().getContentAsString();
        responseListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);
        assertEquals(responseListing.getId(), listingId);
        assertEquals( tagCountBeforeDelete - 1, responseListing.getTags().size());
    }

    @Test
    @WithMockUser(username = DataInitializer.TESTING_USER2_EMAIL, roles = "USER")
    public void testAddBidToListingAndRemoveIt() throws Exception {
        Listing listing = listingRepository.findAll().get(0);
        long listingId = listing.getId();
        Bid bid = new Bid();
        bid.setPrice(100);
        bid.setDescription("I will 3d print you a car");
        bid.setDaysToDeliver(7);
        bid.setBidder(userRepository.findAll().get(0));

        BidCreateRequest bidCreateRequest = BidCreateRequest.fromBid(bid);
        bidCreateRequest.setListingId(listingId);
        String json = new ObjectMapper().writeValueAsString(bidCreateRequest);

        // bid count before
        int bidCountBefore = listingRepository.findById(listingId).get().getBids().size();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/private/" + listingId + "/bids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        String responseContent = result.getResponse().getContentAsString();
        ListingDTO responseListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);

        assertEquals(bidCountBefore + 1, responseListing.getBids().size());

        long bidId = responseListing.getBids().stream().filter(b -> b.getDescription().equals(bid.getDescription())).findFirst().get().getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/private/" + listingId + "/bids/" + bidId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/" + listingId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        responseContent = result.getResponse().getContentAsString();
        responseListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);
        assertEquals(responseListing.getId(), listingId);
        assertEquals(bidCountBefore, responseListing.getBids().size());
    }

    @Test
    public void testAddPhotosAndRemoveOne() throws Exception {
        long listingId = 1L;
        long photosAddedByDataInit = listingRepository.findById(listingId).orElseThrow().getPhotos().size();

        MockMultipartFile kraftowyKowal = null;
        MockMultipartFile kraftowaJava = null;
        try {
            ClassPathResource photoResource = new ClassPathResource("test-photos/kraftowy_kowal.jpg");
            byte[] photoBytes = StreamUtils.copyToByteArray(photoResource.getInputStream());
            kraftowyKowal = new MockMultipartFile(
                    "photos",
                    photoResource.getFilename(),
                    "image/jpeg",
                    photoBytes
            );

            photoResource = new ClassPathResource("test-photos/kraftowa_java.jpg");
            photoBytes = StreamUtils.copyToByteArray(photoResource.getInputStream());
            kraftowaJava = new MockMultipartFile(
                    "photos",
                    photoResource.getFilename(),
                    "image/jpeg",
                    photoBytes
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(kraftowyKowal);
        assertNotNull(kraftowaJava);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("photos", kraftowyKowal.getOriginalFilename());
        params.add("photos", kraftowaJava.getOriginalFilename());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/private/" + listingId + "/photos")
                        .file(kraftowyKowal)
                        .file(kraftowaJava)
                        .params(params))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Listing updatedListing = listingRepository.findById(listingId).orElseThrow();
        assertEquals(2 + photosAddedByDataInit, updatedListing.getPhotos().size());
        assertNotNull(updatedListing.getPhotos().get(0));
        assertNotNull(updatedListing.getPhotos().get(1));

        String responseContent = result.getResponse().getContentAsString();
        ListingDTO responseListings = new ObjectMapper().readValue(responseContent, ListingDTO.class);
        Collection<String> responsePhotos = responseListings.getPhotos();

        /* Remove photos */
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/private/" + listingId + "/photos")
                        .param("photoPath", responsePhotos.iterator().next()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Listing photosRemovedListing = listingRepository.findById(listingId).orElseThrow();
        assertEquals(1 + photosAddedByDataInit, photosRemovedListing.getPhotos().size());
    }

    /* edge case test for the situation when photos list is not null */
    @Test
    public void testAddPhotosOneByOne() throws Exception {
        long listingId = 1L;
        long photosAddedByDataInit = listingRepository.findById(listingId).orElseThrow().getPhotos().size();

        MockMultipartFile kraftowyKowal = null;
        MockMultipartFile kraftowaJava = null;
        try {
            ClassPathResource photoResource = new ClassPathResource("test-photos/kraftowy_kowal.jpg");
            byte[] photoBytes = StreamUtils.copyToByteArray(photoResource.getInputStream());
            kraftowyKowal = new MockMultipartFile(
                    "photos",
                    photoResource.getFilename(),
                    "image/jpeg",
                    photoBytes
            );

            photoResource = new ClassPathResource("test-photos/kraftowa_java.jpg");
            photoBytes = StreamUtils.copyToByteArray(photoResource.getInputStream());
            kraftowaJava = new MockMultipartFile(
                    "photos",
                    photoResource.getFilename(),
                    "image/jpeg",
                    photoBytes
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(kraftowyKowal);
        assertNotNull(kraftowaJava);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/private/" + listingId + "/photos")
                        .file(kraftowyKowal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/private/" + listingId + "/photos")
                        .file(kraftowaJava))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Listing updatedListing = listingRepository.findById(listingId).orElseThrow();
        assertEquals(2 + photosAddedByDataInit, updatedListing.getPhotos().size());
        assertNotNull(updatedListing.getPhotos().get(0));
        assertNotNull(updatedListing.getPhotos().get(1));

        String responseContent = result.getResponse().getContentAsString();
        ListingDTO responseListings = new ObjectMapper().readValue(responseContent, ListingDTO.class);
        Collection<String> responsePhotos = responseListings.getPhotos();

        /* Remove photos */
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/private/" + listingId + "/photos")
                        .param("photoPath", responsePhotos.iterator().next()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Listing photosRemovedListing = listingRepository.findById(listingId).orElseThrow();
        assertEquals(1 + photosAddedByDataInit, photosRemovedListing.getPhotos().size());

        /* add one again */
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/private/" + listingId + "/photos")
                        .file(kraftowyKowal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        photosRemovedListing = listingRepository.findById(listingId).orElseThrow();
        assertEquals(2 + photosAddedByDataInit, photosRemovedListing.getPhotos().size());
    }

    @Test
    public void testSearchByBidAvgBetween() throws Exception {
        double minPrice = 50.0;
        double maxPrice = 100.0;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/search?minPrice=" + minPrice + "&maxPrice=" + maxPrice))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ListingDTO> responseListings = new ObjectMapper().readValue(responseContent, new TypeReference<>() {
        });

        assertNotNull(responseListings);
        for (ListingDTO listing : responseListings) {
            double avg = 0;
            for (BidDTO bid : listing.getBids()) {
                avg += bid.getPrice();
            }
            avg /= listing.getBids().size();
            assertTrue(avg >= minPrice && avg <= maxPrice);
        }
    }

    @Test
    public void testSearchByBidAvgMinMaxPrice() throws Exception {
        double minPrice = 0.0;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/search?minPrice=" + minPrice))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ListingDTO> responseListings = new ObjectMapper().readValue(responseContent, new TypeReference<>() {
        });

        assertNotNull(responseListings);
        for (ListingDTO listing : responseListings) {
            double avg = 0;
            for (BidDTO bid : listing.getBids()) {
                avg += bid.getPrice();
            }
            avg /= listing.getBids().size();
            assertTrue(avg >= minPrice);
        }

        double maxPrice = 400.0;

        result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/search?maxPrice=" + maxPrice))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        responseContent = result.getResponse().getContentAsString();
        responseListings = new ObjectMapper().readValue(responseContent, new TypeReference<>() {
        });

        assertNotNull(responseListings);
        for (ListingDTO listing : responseListings) {
            double avg = 0;
            for (BidDTO bid : listing.getBids()) {
                avg += bid.getPrice();
            }
            avg /= listing.getBids().size();
            assertTrue(avg <= maxPrice);
        }
    }

}
