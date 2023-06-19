package com.ecom.craftbid.integration;

import com.ecom.craftbid.dtos.BidCreateRequest;
import com.ecom.craftbid.dtos.BidDTO;
import com.ecom.craftbid.entities.listing.Bid;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class BidControllerTest extends BaseIntegrationTest {

    @Test
    @WithMockUser(username = "john@example.com", password = "pass", roles = "USER")
    public void testCreateAndDeleteBid() throws Exception {
        BidCreateRequest bidCreateRequest = new BidCreateRequest();
        bidCreateRequest.setPrice(100);
        bidCreateRequest.setListingId(1);
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
    @WithMockUser(username = "john@example.com", password = "pass", roles = "USER")
    public void createAndGetBidThenAllBids() throws Exception {
        final int bidsAddedByDataInit = bidRepository.findAll().size();

        BidDTO bidDTO = newBidCreateDTO(100, "test bid");

        BidDTO bidGetByIdResult = new ObjectMapper().readValue(
                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/bids/" + bidDTO.getId()))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                BidDTO.class);

        assertNotNull(bidGetByIdResult);

        newBidCreateDTO(200, "test bid 2");
        newBidCreateDTO(300, "test bid 3");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/bids"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<BidDTO> bidDTOList = new ObjectMapper().readValue(responseContent, new TypeReference<>() {
        });

        assertNotNull(bidDTOList);
        assertEquals(3 + bidsAddedByDataInit, bidDTOList.size());
    }

    @Test
    @WithMockUser(username = "john@example.com", password = "pass", roles = "USER")
    public void getUsersBids() throws Exception {
        long userId = 1;
        int bidsAddedByDataInit = bidRepository.findByBidderId(userId).size();
        newBidCreateDTO(100, "test bid");
        newBidCreateDTO(200, "test bid 2");
        newBidCreateDTO(300, "test bid 3");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/bids/user/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<BidDTO> bidDTOList = new ObjectMapper().readValue(responseContent, new TypeReference<>() {
        });

        assertNotNull(bidDTOList);
        assertEquals(3 + bidsAddedByDataInit, bidDTOList.size());
        assertEquals(bidDTOList.get(bidsAddedByDataInit).getDescription(), "test bid");
        assertEquals(bidDTOList.get(1 + bidsAddedByDataInit).getDescription(), "test bid 2");
        assertEquals(bidDTOList.get(2 + bidsAddedByDataInit).getDescription(), "test bid 3");
    }

    @WithMockUser(username = "john@example.com", password = "pass", roles = "USER")
    private BidDTO newBidCreateDTO(long price, String description) throws Exception {
        BidCreateRequest bidCreateRequest1 = new BidCreateRequest();
        bidCreateRequest1.setPrice(price);
        bidCreateRequest1.setListingId(1);
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
