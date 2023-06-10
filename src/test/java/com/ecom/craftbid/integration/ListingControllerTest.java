package com.ecom.craftbid.integration;

import com.ecom.craftbid.dtos.ListingCreateRequest;
import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.repositories.ListingRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        List<ListingDTO> responseListings = new ObjectMapper().readValue(responseContent, new TypeReference<List<ListingDTO>>() {});

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
        List<ListingDTO> responseListings = new ObjectMapper().readValue(responseContent, new TypeReference<List<ListingDTO>>() {});

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



}
