package com.ecom.craftbid.integration;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class DemoControllerTest extends BaseIntegrationTest{
    @Test
    @WithMockUser(username = "john@example.com", password = "pass", roles = "USER")
    public void testProtectedEndpoint() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/hello-world"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello from secured World: john@example.com!"));
    }
}
