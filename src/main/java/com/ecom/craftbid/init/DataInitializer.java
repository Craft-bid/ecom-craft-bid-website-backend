package com.ecom.craftbid.init;

import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.repositories.BidRepository;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, ListingRepository listingRepository,
                           BidRepository bidRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.bidRepository = bidRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeListingsAndBids();
        List<Listing> listings = listingRepository.findAll();
        System.out.println("fsfsdfsd");
    }

    private void initializeUsers() {
        User user1 = User.builder()
                .email("john@example.com")
                .password("1234")
                .name("JohnDoe")
                .build();
        User user2 = User.builder()
                .email("jane@example.com")
                .password("1234")
                .name("JaneDoe")
                .build();

        userRepository.saveAll(Arrays.asList(user1, user2));
    }

    private void initializeListingsAndBids() {
        User user1 = userRepository.findByEmail("john@example.com").orElse(null);
        User user2 = userRepository.findByEmail("jane@example.com").orElse(null);

        if (user1 != null && user2 != null) {
            Listing listing1 = Listing.builder()
                    .title("Item 1")
                    .description("Description for Item 1")
                    .advertiser(user1)
                    .build();
            Listing listing2 = Listing.builder()
                    .title("Item 2")
                    .description("Description for Item 2")
                    .advertiser(user1)
                    .build();
            Listing listing3 = Listing.builder()
                    .title("Item 3")
                    .description("Description for Item 3")
                    .advertiser(user2)
                    .build();

            listingRepository.saveAll(Arrays.asList(listing1, listing2, listing3));

            Bid bid1 = Bid.builder()
                    .bidder(user2)
                    .listing(listing1)
                    .price(100)
                    .build();
            Bid bid2 = Bid.builder()
                    .bidder(user2)
                    .listing(listing2)
                    .price(150)
                    .build();

            bidRepository.saveAll(Arrays.asList(bid1, bid2));
        }
    }
}