package com.ecom.craftbid.init;

import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.user.*;
import com.ecom.craftbid.enums.FeedbackStar;
import com.ecom.craftbid.enums.Role;
import com.ecom.craftbid.repositories.BidRepository;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final PasswordEncoder passwordEncoder;
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public DataInitializer(UserRepository userRepository, ListingRepository listingRepository,
                           BidRepository bidRepository, PasswordEncoder passwordEncoder,
                           FeedbackRepository feedbackRepository) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.bidRepository = bidRepository;
        this.passwordEncoder = passwordEncoder;
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeListingsAndBids();
        System.out.println("Example data initialized");
    }

    private void initializeUsers() {
        Profile profile1 = Profile.builder()
                .averageRating(4.0)
                .description("Hello my name is John Doe. I am a 3D printing enthusiast and I love to create new things.")
                .build();

        User user1 = new User();
        user1.setEmail("john@example.com");
        user1.setRole(Role.USER);
        user1.setPassword(passwordEncoder.encode("1234"));
        user1.setProfile(profile1);

        profile1.setUser(user1);

        Profile profile2 = new Profile();
        profile2.setAverageRating(4.5);
        profile2.setDescription("Hello my name is Jane Doe. I am a 3D printing enthusiast and I love to create new things.");

        User user2 = new User();
        user2.setEmail("jane@example.com");
        user2.setRole(Role.USER);
        user2.setPassword(passwordEncoder.encode("1234"));
        user2.setProfile(profile2);

        profile2.setUser(user2);


        userRepository.saveAll(Arrays.asList(user1, user2));

        PersonalData personalData1 = PersonalData.builder()
                .address("123 Main Street")
                .city("Lodz")
                .country("Poland")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .zipCode("99-200")
                .build();

        PersonalData personalData2 = PersonalData.builder()
                .address("Politechniki 81")
                .city("Warszawa")
                .country("Poland")
                .firstName("Kacper")
                .lastName("Johanson")
                .phoneNumber("999454901")
                .zipCode("12-100")
                .build();


        user1.getProfile().addPersonalData(personalData1);
        user2.getProfile().addPersonalData(personalData2);


        Feedback feedback1 = new Feedback();
        feedback1.setComment("Great seller, would buy again!");
        feedback1.setStars(FeedbackStar.FOUR_STARS);
        feedback1.setAuthor(user1.getProfile());
        feedback1.setReceiver(user2.getProfile());

        user2.addReceivedFeedback(feedback1);


        Feedback feedback2 = new Feedback();
        feedback2.setComment("Yeah i can recommend this seller");
        feedback2.setStars(FeedbackStar.FIVE_STARS);
        feedback2.setAuthor(user2.getProfile());
        feedback2.setReceiver(user1.getProfile());

        user1.addReceivedFeedback(feedback2);

        userRepository.saveAll(Arrays.asList(user1, user2));


    }

    private void initializeListingsAndBids() {
        User user1 = userRepository.findByEmail("john@example.com").orElse(null);
        User user2 = userRepository.findByEmail("jane@example.com").orElse(null);

        if (user1 != null && user2 != null) {
            Listing listing1 = Listing.builder()
                    .title("Item 1")
                    .creationDate(new java.util.Date(System.currentTimeMillis()))
                    .description("Description for Item 1")
                    .ended(false)
                    .expirationDate(new java.util.Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                    .advertiser(user1)
                    .build();
            listing1.addPhoto("https://www.kisscom.co.uk/media/pages/news/3d-printing-a-world-of-possibilities/5824bcf0fe-1666776023/blog_31.08.18.png");
            listing1.addPhoto("https://wpvip.edutopia.org/wp-content/uploads/2022/11/shutterstock_1668411985-crop.jpg");


            Listing listing2 = Listing.builder()
                    .title("Item 2")
                    .creationDate(new Date(System.currentTimeMillis() - 1000000000))
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