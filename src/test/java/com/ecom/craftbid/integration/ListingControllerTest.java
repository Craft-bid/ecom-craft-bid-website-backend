package com.ecom.craftbid.integration;

import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.dtos.ListingResponse;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.repositories.ListingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void testSearchNoCriteria() throws Exception { // no criteria equivalent to findAll
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/search"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ListingResponse listingResponse = objectMapper.readValue(responseContent, ListingResponse.class);
        List<ListingDTO> responseListings = listingResponse.getContent();

        // DataInitializer creates 3 listings
        assertEquals(3, responseListings.size());
    }
}
