package com.ecom.craftbid.integration;

import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.exceptions.NotFoundException;
import com.ecom.craftbid.init.DataInitializer;
import com.ecom.craftbid.repositories.TagRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
@WithMockUser(username = DataInitializer.TESTING_USER2_EMAIL, roles = "USER")
public class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testCreateAndDeleteTag() throws Exception {
        String name = "testTag";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/private/tags")
            .contentType(MediaType.APPLICATION_JSON)
            .content(name))
            .andExpect(MockMvcResultMatchers.status().isOk());

        Tag tag = tagRepository.findByName(name).orElseThrow(NotFoundException::new);
        assertNotNull(tag);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/private/tags" + "/" + tag.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        tag = tagRepository.findByName(name).orElse(null);
        assertNull(tag);
    }

    @Test
    public void testGetAllTags() throws Exception {
        long tagsAddedByDataInit = tagRepository.count();
        addTag("testTag1");
        addTag("testTag2");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/tags"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<Tag> responseTags = mapper.readValue(responseContent, new TypeReference<>() {});
        assertEquals(2 + tagsAddedByDataInit, responseTags.size());
    }

    private void addTag(String name) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/private/tags")
            .contentType(MediaType.APPLICATION_JSON)
            .content(name))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
