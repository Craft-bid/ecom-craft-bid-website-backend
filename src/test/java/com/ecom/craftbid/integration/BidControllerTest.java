package com.ecom.craftbid.integration;

import com.ecom.craftbid.dtos.BidCreateRequest;
import com.ecom.craftbid.dtos.BidDTO;
import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.repositories.BidRepository;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void createAndGetBidThenAllBids() throws Exception {
        BidDTO bidDTO = createBid(100, "test bid");

        BidDTO bidGetByIdResult = new ObjectMapper().readValue(
                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/bids/" + bidDTO.getId()))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                BidDTO.class);

        assertNotNull(bidGetByIdResult);

        createBid(200, "test bid 2");
        createBid(300, "test bid 3");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/bids"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<BidDTO> bidDTOList = new ObjectMapper().readValue(responseContent, new TypeReference<List<BidDTO>>() {
        });

        assertNotNull(bidDTOList);
        final int bidsAddedByDataInit = 2;
        assertEquals(3 + bidsAddedByDataInit, bidDTOList.size());
        assertEquals(bidDTOList.get(2).getDescription(), "test bid");
        for (int i = 3; i < bidDTOList.size(); i++) {
            assertEquals(bidDTOList.get(i).getDescription(), "test bid " + (i - 1));
        }
    }

    private BidDTO createBid(long price, String description) throws Exception {
        BidCreateRequest bidCreateRequest1 = new BidCreateRequest();
        bidCreateRequest1.setPrice(price);
        bidCreateRequest1.setListingId(1);
        bidCreateRequest1.setBidderId(1);
        bidCreateRequest1.setDaysToDeliver(10);
        bidCreateRequest1.setDescription(description);

        String json = new ObjectMapper().writeValueAsString(bidCreateRequest1);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/private/bids")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        return new ObjectMapper().readValue(responseContent, BidDTO.class);
    }

}
