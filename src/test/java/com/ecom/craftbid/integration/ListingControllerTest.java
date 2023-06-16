package com.ecom.craftbid.integration;

import com.ecom.craftbid.dtos.ListingCreateRequest;
import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.dtos.ListingUpdateRequest;
import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private EntityManager entityManager;

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
        List<ListingDTO> responseListings = new ObjectMapper().readValue(responseContent, new TypeReference<List<ListingDTO>>() {
        });

        List<Listing> listings = listingRepository.findAll();
        assertEquals(listings.size(), responseListings.size());
    }

    @Test
    public void testSearchByTitle() throws Exception {
        String title = "Item 1";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/search?title=" + title))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ListingDTO> responseListings = new ObjectMapper().readValue(responseContent, new TypeReference<List<ListingDTO>>() {
        });

        assertEquals(1, responseListings.size());
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

        Listing listing = createListing();

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
            Listing listing = createListing();

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
    public void testAddPhotosAndRemoveThem() throws Exception {
        long listingId = 1L;
        Listing listing = listingRepository.findById(listingId).orElseThrow();
        assertEquals(0, listing.getPhotos().size());

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
        assertEquals(2, updatedListing.getPhotos().size());
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
        assertEquals(1, photosRemovedListing.getPhotos().size());
    }

    private Listing createListing() {
        Listing listing = new Listing();
        listing.setTitle("Test Listing");
        listing.setEnded(false);
        listing.setDescription("Test Description");
        listing.setAdvertiser(entityManager.getReference(User.class, 1L));
        listing.setBids(List.of(entityManager.getReference(Bid.class, 1L)));

        return listingRepository.save(listing);
    }

}
