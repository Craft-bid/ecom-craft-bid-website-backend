package com.ecom.craftbid.init;

import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.entities.user.*;
import com.ecom.craftbid.enums.FeedbackStar;
import com.ecom.craftbid.enums.Role;
import com.ecom.craftbid.repositories.BidRepository;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.TagRepository;
import com.ecom.craftbid.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class DataInitializer implements CommandLineRunner {
    public static final String TESTING_USER2_EMAIL = "jane@example.com";
    public static final String TESTING_USER_EMAIL = "john@example.com";
    private final String defaultProfPic = "https://icon-library.com/images/default-profile-icon/default-profile-icon-24.jpg";

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;
    private final FeedbackRepository feedbackRepository;

    @Autowired
    private Environment environment;

    @Autowired
    public DataInitializer(UserRepository userRepository, ListingRepository listingRepository,
                           BidRepository bidRepository, PasswordEncoder passwordEncoder,
                           FeedbackRepository feedbackRepository, TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.bidRepository = bidRepository;
        this.passwordEncoder = passwordEncoder;
        this.feedbackRepository = feedbackRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if ("create-drop".equalsIgnoreCase(environment.getProperty("spring.jpa.hibernate.ddl-auto"))) {
            initializeUsers();
            //initializeListingsAndBids();
            initializePopulatedListings();
            System.out.println("Example data initialized");
        }

    }

    private void initializeUsers() {
        Profile profile1 = Profile.builder()
                .averageRating(4.0)
                .description("Hello my name is John Doe. I am a 3D printing enthusiast and I love to create new things.")
                .build();

        User user1 = new User();
        user1.setEmail(TESTING_USER_EMAIL);
        user1.setRole(Role.USER);
        user1.setPassword(passwordEncoder.encode("1234"));
        user1.setProfile(profile1);

        user1.setProfile(profile1);

        Profile profile2 = new Profile();
        profile2.setAverageRating(4.5);
        profile2.setDescription("Hello my name is Jane Doe. I am a 3D printing enthusiast and I love to create new things.");

        User user2 = new User();
        user2.setEmail(TESTING_USER2_EMAIL);
        user2.setRole(Role.USER);
        user2.setPassword(passwordEncoder.encode("1234"));
        user2.setProfile(profile2);

        user2.setProfile(profile2);

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

    private void initializePopulatedListings() {
        /* universal tags */
        Tag cnc = createTag("CNCOperating");
        Tag wood = createTag("Woodworking");
        Tag machining = createTag("Machining");
        Tag polishing = createTag("Polishing");
        Tag metal = createTag("Metalworking");
        Tag eng = createTag("English");
        Tag pl = createTag("Polish");
        Tag ger = createTag("German");
        Tag fullTime = createTag("Full Time");
        Tag partTime = createTag("Part Time");

        User georgeTheBidder = createUser("george@yahoo.com", "georgy2137", 5, "Hello my name is George. I like to buy things.",
                "201 W Washington Blvd", "Los Angeles", "USA", "George", "Bidman", "123456789", "CA90007",
                "Great buyer, quick payment", FeedbackStar.FIVE_STARS, Role.USER);
        User pawelKrawczyk = createUser("pawel.krawczyk@wp.pl", "kraftowyPawel", 5, "Hi I'm Pawel Krawczyk!",
                "Politechniki 81", "Warszawa", "Poland", "Pawel", "Krawczyk", "123456789", "12-100",
                "nice", FeedbackStar.FIVE_STARS, Role.USER);
        User jamesWilson = createUser("james@uk.co", "jamesOneTwo3", 5, "Hello my name is James Wilson. I am professional woodworker and I love to create new things.",
                "221B Baker Street ", "London", "UK", "James", "Wilson", "123456789", "99-200",
                "Great seller, would buy again!", FeedbackStar.FIVE_STARS, Role.USER);
        User elonProCNC = createUser("elon@gmail.com", "elonNowak", 5, "Hello my name is Elon. I am professional CNC operator with 10 years of experience.",
                "23A Millers", "Brussels", "Belgium", "Elon", "Nowak", "31353142", "29312",
                "Great products", FeedbackStar.FIVE_STARS, Role.ADMIN);

        /* listing */
        List<String> photosJames = new ArrayList<>();
        photosJames.add("https://cdn3.coco-papaya.com/32374-thickbox_default/moai-statue-20cm-in-suar-wood.jpg");
        photosJames.add("https://img.freepik.com/premium-photo/traditional-wooden-statue-moai-from-easter-island-dark-background_118047-8551.jpg");
        Listing listingJames = createListing("Wooden statues", new Date(System.currentTimeMillis() - 1000000000), "Commission me to create a wooden statue for you!", jamesWilson, photosJames);
        listingJames.addTag(wood);
        listingJames.addTag(polishing);
        listingJames.addTag(eng);

        Bid bidJames = createBid(georgeTheBidder, listingJames, 100, "I want one");
        Bid bidJames2 = createBid(pawelKrawczyk, listingJames, 150, "");
        Bid bidJames3 = createBid(georgeTheBidder, listingJames, 200, "");
        listingRepository.save(listingJames);

        /* another listing */
        List<String> photosPawel = new ArrayList<>();
        photosPawel.add("https://p.globalsources.com/IMAGES/PDT/B1188408209/custom-keyboard-for-CNC.jpg");
        Listing listingPawel = createListing("CNC milling", new Date(System.currentTimeMillis() - 1000000000), "I need someone to machine me a case", pawelKrawczyk, photosPawel);
        listingPawel.addTag(cnc);
        listingPawel.addTag(machining);
        listingPawel.addTag(pl);

        Bid bidPawel = createBid(jamesWilson, listingPawel, 300, "I know i guy who can do it");
        Bid bidPawel2 = createBid(elonProCNC, listingPawel, 600, "Checkout my profile");
        listingRepository.save(listingPawel);

        /* another listing */
        List<String> photosElon = new ArrayList<>();
        photosElon.add("https://cdn3.coco-papaya.com/32374-thickbox_default/moai-statue-20cm-in-suar-wood.jpg");
        Listing listingElon = createListing("Wooden statues", new Date(System.currentTimeMillis() - 2000000), "In need of a wooden statue", elonProCNC, photosElon);
        listingElon.addTag(wood);
        listingElon.addTag(eng);

        Bid bidElon = createBid(jamesWilson, listingElon, 500, "I create such statues");
        listingRepository.save(listingElon);

        /* another listing */
        List<String> photosGeorge = new ArrayList<>();
        photosGeorge.add("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/CNC_machine.jpg/1200px-CNC_machine.jpg");
        Listing listingGeorge = createListing("In need of top CNC machine", new Date(System.currentTimeMillis()), "I need help of experienced cnc machine factory to provide one for my workshop.", georgeTheBidder, photosGeorge);
        listingGeorge.addTag(cnc);
        listingGeorge.addTag(machining);
        listingGeorge.addTag(eng);

        Bid bidGeorge = createBid(elonProCNC, listingGeorge, 5000, "I have one to sell");
        listingRepository.save(listingGeorge);

        /* another listing */
        List<String> photosGeorge2 = new ArrayList<>();
        photosGeorge2.add("https://exn9cid2c47.exactdn.com/wp-content/uploads/2022/03/laser-parts-hero-1-768x562.jpg?strip=all&lossy=1&ssl=1");
        photosGeorge2.add("https://sendcutsend.com/wp-content/uploads/2022/01/stainless-hero-1.jpg");
        Listing listingGeorge2 = createListing("Need two sets of metal parts", new Date(System.currentTimeMillis()), "I need someone to provide me with two sets of metal parts, they will look some like the ones from the photos", georgeTheBidder, photosGeorge2);
        listingGeorge2.addTag(metal);
        listingGeorge2.addTag(pl);
        listingRepository.save(listingGeorge2);

        /* another listing */
        List<String> photosJames2 = new ArrayList<>();
        photosJames2.add("https://bwp.lt/wp-content/uploads/2020/04/4Pcs-Universal-Solid-Home-Table-Feet-Right-Angle-Square-Wooden-DIY-Replacement-Furniture-Leg-Tool-Reliable.jpg");
        Listing listingJames2 = createListing("Will create wooden furniture legs", new Date(System.currentTimeMillis()), "Willing to create custom furniture legs", jamesWilson, photosJames2);
        listingJames2.addTag(wood);
        listingJames2.addTag(eng);

        Bid bidJames4 = createBid(pawelKrawczyk, listingJames2, 150, "");
        listingRepository.save(listingJames2);
    }

    private User createUser(String mail, String password, double averageRating, String description, String address,
                            String city, String country, String firstName, String lastName, String number, String zipCode,
                            String comment, FeedbackStar feedbackStar, Role role) {
        User user = new User();
        user.setEmail(mail);
        user.setRole(Role.USER);
        user.setDisplayName(firstName + " " + lastName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        Profile profile = Profile.builder()
                .averageRating(averageRating)
                .description(description)
                .avatarUri(defaultProfPic)
                .build();
        user.setProfile(profile);
        userRepository.save(user);

        PersonalData personalData = PersonalData.builder()
                .address(address)
                .city(city)
                .country(country)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(number)
                .zipCode(zipCode)
                .build();
        user.getProfile().addPersonalData(personalData);
        userRepository.save(user);

        Feedback feedback = new Feedback();
        feedback.setComment(comment);
        feedback.setStars(feedbackStar);
        feedback.setAuthor(user.getProfile());
        feedback.setReceiver(user.getProfile());

        user.addReceivedFeedback(feedback);
        userRepository.save(user);

        return user;
    }

    private Tag createTag(String name) {
        Tag tag = Tag.builder()
                .name(name)
                .build();
        tagRepository.save(tag);

        return tag;
    }

    private Bid createBid(User bidder, Listing listing, int price, String description) {
        Random random = new Random();
        Bid bid = Bid.builder()
                .bidder(bidder)
                .listing(listing)
                .price(price)
                .creationDate(new Date())
                .description(description)
                .daysToDeliver(random.nextInt(10))
                .build();
        bidRepository.save(bid);

        return bid;
    }


    private Listing createListing(String title, Date creationDate, String description, User advertiser, List<String> photos) {
        Listing listing2 = Listing.builder()
                .title(title)
                .creationDate(creationDate)
                .expirationDate(new Date(creationDate.getTime() + 1000000000))
                .description(description)
                .advertiser(advertiser)
                .ended(false)
                .build();
        listingRepository.save(listing2);

        if (photos != null) {
            for (String photo : photos) {
                listing2.addPhoto(photo);
            }
        }

        return listing2;
    }
}