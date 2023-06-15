package com.ecom.craftbid.integration;

import com.ecom.craftbid.dtos.BidCreateRequest;
import com.ecom.craftbid.dtos.BidDTO;
import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.repositories.BidRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class BidControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testCreateAndDeleteBid() throws Exception {
        BidCreateRequest bidCreateRequest = new BidCreateRequest();
        bidCreateRequest.setPrice(100);
        bidCreateRequest.setListingId(1);
        bidCreateRequest.setBidderId(1);
        bidCreateRequest.setDaysToDeliver(10);
        bidCreateRequest.setDescription("test bid");

        String json = new ObjectMapper().writeValueAsString(bidCreateRequest);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/private/bids")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        BidDTO bidDTO = new ObjectMapper().readValue(responseContent, BidDTO.class);

        assertNotNull(bidDTO);
        System.out.println(bidDTO.getCreationDate());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/private/bids" + "/" + bidDTO.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Bid bid = bidRepository.findById(bidDTO.getId()).orElse(null);
        assertNull(bid);
    }
}
