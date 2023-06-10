package com.ecom.craftbid;

import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.repositories.ListingRepository;
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

import java.util.Set;

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
        Listing listing = listingRepository.findById(1L);
        Set<Tag> tags = listing.getTags();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/listings/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        //todo assert some data from response
    }
}
