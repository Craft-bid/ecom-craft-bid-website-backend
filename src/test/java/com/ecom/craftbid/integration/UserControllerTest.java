package com.ecom.craftbid.integration;

import com.ecom.craftbid.dtos.AuthenticationRequest;
import com.ecom.craftbid.dtos.AuthenticationResponse;
import com.ecom.craftbid.dtos.RegisterRequest;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.repositories.UserRepository;
import com.ecom.craftbid.services.AuthenticationService;
import com.ecom.craftbid.services.UserService;
import com.ecom.craftbid.utils.TokenParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetUserIdFromToken() throws Exception {
        /* register the user */
        String token = "token69";
        String email = "darthjava@springboot.com";
        String password = "darthjava2137";
        String username = "darthjava";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name(username)
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        /* auth the user */
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andReturn();

        /* get the user id from the token */
        String tokenResponse = result.getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(tokenResponse, AuthenticationResponse.class);
        String tokenFromResponse = authenticationResponse.getToken();

        String parsedEmail = TokenParser.getEmailFromToken(tokenFromResponse);
        assertEquals(email, parsedEmail);

        Optional<User> user = userRepository.findByEmail(parsedEmail);
        assertNotNull(user);

        Long userId = user.get().getId();
        assertNotNull(userId);

        assertEquals(userId, userService.getMyId(tokenFromResponse));
    }
}
