package com.ecom.craftbid;

import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.repositories.ListingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ListingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ListingRepository listingRepository;
    @Test
    public void testGetListingById() throws Exception {
        long id = 1;
        Listing listing = listingRepository.findById(id);
        Set<Tag> tags = listing.getTags();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ListingDTO responseListing = new ObjectMapper().readValue(responseContent, ListingDTO.class);

        assertEquals(listing.getId(), responseListing.getId());
        assertEquals(listing.getTags().size(), responseListing.getTags().size());
    }
}
