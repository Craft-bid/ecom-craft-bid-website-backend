package com.ecom.craftbid.integration;


import com.ecom.craftbid.dtos.AuthenticationResponse;
import com.ecom.craftbid.dtos.RegisterRequest;
import com.ecom.craftbid.dtos.UserDTO;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.enums.Role;
import com.ecom.craftbid.repositories.UserRepository;
import com.ecom.craftbid.services.AuthenticationService;
import com.ecom.craftbid.services.UserService;
import com.ecom.craftbid.utils.TokenParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class UserControllerTest {
    @Value("${secureTokenSIgnKey}")
    private String SECRET_KEY;
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
        String email = "darthjava@springboot.com";
        String password = "darthjava2137";
        String username = "darthjava";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name(username)
                .email(email)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        /* get the user id from the token */
        String tokenResponse = result.getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(tokenResponse, AuthenticationResponse.class);
        String tokenFromResponse = authenticationResponse.getToken();

        String parsedEmail = TokenParser.getEmailFromToken(tokenFromResponse, SECRET_KEY);
        assertEquals(email, parsedEmail);

        Optional<User> user = userRepository.findByEmail(parsedEmail);
        assertNotNull(user);

        Long userId = user.get().getId();
        assertNotNull(userId);

        assertEquals(userId, userService.getMyId(tokenFromResponse));

        /* test endpoint */
        result = mockMvc.perform(get("/api/v1/public/users/myId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tokenFromResponse))
                .andExpect(status().isOk())
                .andReturn();

        String userIdResponse = result.getResponse().getContentAsString();
        assertEquals(userId, Long.parseLong(userIdResponse));
    }

    @Test
    public void testGetUserRoleFromToken() throws Exception {
        String email = "james.wilson@roletest.ca";
        String password = "james1@role";
        String username = "jameswilson";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name(username)
                .email(email)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(response, AuthenticationResponse.class);
        String token = authenticationResponse.getToken();

        Role role = TokenParser.getRoleFromToken(token, SECRET_KEY);
        Optional<User> user = userRepository.findByEmail(email);
        assertNotNull(user);
        User jamesWilson = user.get();
        assertEquals(role, jamesWilson.getRole());
    }

    @Test
    public void addPhotoToUser() throws Exception {
        /* register the user */
        String email = "phototest@test.pl";
        String password = "photo!@312";
        String username = "photoTest";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name(username)
                .email(email)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(response, AuthenticationResponse.class);
        String token = authenticationResponse.getToken();

        long userId = userService.getMyId(token);
        User user = userService.getUser(userId);
        assertNotNull(user);

        /* add photo from resources/test-photos/profile_pic.jpg to the user */
        /* post to /public/users/{userId}/photo */
        MockMultipartFile profPic = null;
        try {
            ClassPathResource photoResource = new ClassPathResource("test-photos/profile_pic.jpg");
            byte[] photoBytes = StreamUtils.copyToByteArray(photoResource.getInputStream());
            profPic = new MockMultipartFile(
                    "photo",
                    photoResource.getFilename(),
                    "image/jpeg",
                    photoBytes
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(profPic);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("photo", profPic.getOriginalFilename());

        result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/public/users/" + userId + "/photo")
                    .file(profPic)
                    .params(params))
            .andExpect(status().isOk())
            .andReturn();

        UserDTO userDTO = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
        assertNotNull(userDTO);

        User userWithPhoto = userService.getUser(userId);
        assertNotNull(userWithPhoto);
        assertNotNull(userWithPhoto.getProfile().getAvatarUri());
        assertTrue(userWithPhoto.getProfile().getAvatarUri().contains("profile_pic.jpg"));
    }

    @Test
    public void testAddUserAndRemoveUser() throws Exception {
        String email = "test@test.com";
        String password = "test1234";
        String username = "test";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name(username)
                .email(email)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(response, AuthenticationResponse.class);
        String token = authenticationResponse.getToken();

        long userId = userService.getMyId(token);
        List<User> users = userRepository.findAll();

        mockMvc.perform(delete("/api/v1/private/users/" + userId))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();

        // get the user
        result = mockMvc.perform(get("/api/v1/public/users/" + userId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

    }
}
