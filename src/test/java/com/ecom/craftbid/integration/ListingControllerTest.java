package com.ecom.craftbid.integration;

import com.ecom.craftbid.dtos.ListingCreateRequest;
import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.dtos.ListingUpdateRequest;
import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.repositories.BidRepository;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.TagRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private BidRepository bidRepository;

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
    public void testAddTagsToListingThenRemoveOne() throws Exception {
        Tag tag1 = createTag("1st Tag");
        Tag tag2 = createTag("2nd Tag");
        List<Long> tags = new ArrayList<>();
        tags.add(tag1.getId());
        tags.add(tag2.getId());
        long listingId = 1;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/private/" + listingId + "/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(tags)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ListingDTO responseListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);

        assertEquals(tags.size(), responseListing.getTags().size());

        long tagIdToRemove = tag1.getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/private/" + listingId + "/tags/" + tagIdToRemove))
                .andExpect(MockMvcResultMatchers.status().isOk());

        result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/" + listingId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        responseContent = result.getResponse().getContentAsString();
        responseListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);
        assertEquals(responseListing.getId(), listingId);
        assertEquals(tags.size() - 1, responseListing.getTags().size());
    }

    @Test
    public void testAddBidToListingAndRemoveIt() throws Exception {
        Listing listing = createListing();
        long listingId = listing.getId();
        Bid bid = createBid("Test Bid", 100, 1, listingId);
        List<Long> bidIds = new ArrayList<>();
        bidIds.add(bid.getId());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/private/" + listingId + "/bids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bidIds)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        String responseContent = result.getResponse().getContentAsString();
        ListingDTO responseListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);

        assertEquals(1, responseListing.getBids().size());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/private/" + listingId + "/bids/" + bid.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/" + listingId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        responseContent = result.getResponse().getContentAsString();
        responseListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);
        assertEquals(responseListing.getId(), listingId);
        assertEquals(0, responseListing.getBids().size());
    }

    private Bid createBid(String description, long price, long bidderId, long listingId) {
        Bid bid = new Bid();
        bid.setDescription(description);
        bid.setPrice(price);
        Date date = new Date();
        bid.setCreationDate(date);
        bid.setDaysToDeliver(1);
        bid.setBidder(entityManager.getReference(User.class, bidderId));
        bid.setListing(entityManager.getReference(Listing.class, listingId));

        return bidRepository.save(bid);
    }

    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setListings(List.of(entityManager.getReference(Listing.class, 1L)));

        return tagRepository.save(tag);
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
