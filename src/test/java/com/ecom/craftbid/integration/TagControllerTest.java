package com.ecom.craftbid.integration;

import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.exceptions.NotFoundException;
import com.ecom.craftbid.repositories.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
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
}
