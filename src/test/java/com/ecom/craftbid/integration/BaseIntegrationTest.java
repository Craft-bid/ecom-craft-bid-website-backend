package com.ecom.craftbid.integration;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.ecom.craftbid.repositories.BidRepository;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.TagRepository;
import com.ecom.craftbid.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {
    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected FilterChainProxy springSecurityFilterChain;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ListingRepository listingRepository;

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected TagRepository tagRepository;

    @Autowired
    protected BidRepository bidRepository;

    @Autowired
    protected UserRepository userRepository;

    @BeforeEach
    public void applySecurity() {
        this.mockMvc = webAppContextSetup(wac)
                .apply(springSecurity(springSecurityFilterChain))
                .build();
    }
}