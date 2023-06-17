package com.ecom.craftbid;

import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.repositories.BidRepository;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.TagRepository;
import com.ecom.craftbid.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class ListingCRUDTests {
    @Autowired
    private BidRepository bidRepository;
    /* if set to true it clears the whole database before each test */
    static final boolean DEBUG = true;

    @Autowired
    ListingRepository listingRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        if (DEBUG) {
            cleanup();
            createSampleDatabase();
        }
    }

    void createSampleDatabase() {
        Listing car3DListing = Listing.builder()
                .title("car 3d printing")
                .description("I will 3d print you a car")
                .ended(false)
                .creationDate(new java.util.Date(System.currentTimeMillis()))
                .expirationDate(new java.util.Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                .build();
        car3DListing.addPhoto("https://i.imgur.com/3d3d.png");
        listingRepository.save(car3DListing);

        Tag car3DTag = Tag.builder().name("3d printing").build();
        car3DListing.addTag(car3DTag);
        tagRepository.save(car3DTag);

        car3DListing.addTag(car3DTag);
        listingRepository.save(car3DListing);

    }

    void cleanup() {
        listingRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void checkListingWithTagCreation() {
        assert(listingRepository.count() == 1);
        assert(tagRepository.count() == 1);

        Listing card3DListing = listingRepository.findAll().get(0);
        Tag car3DTag = tagRepository.findAll().get(0);
        log.info("Listing: " + card3DListing.getTitle());
        log.info("Tag: " + car3DTag.getName());
    }

    @Test
    void checkListingWithEndedValue() {
        Listing card3DListing = listingRepository.findAll().get(0);
        Pageable pageable = PageRequest.of(0, 10);

        assert listingRepository.findByEndedTrue(pageable).getTotalElements() == 0;
        assert listingRepository.findByEndedFalse(pageable).getTotalElements() == 1;

        card3DListing.setEnded(true);
        listingRepository.save(card3DListing);
        assert listingRepository.findByEndedTrue(pageable).getTotalElements() == 1;
        assert listingRepository.findByEndedFalse(pageable).getTotalElements() == 0;
    }


    void createAndSetAdvertiserAndWinnerUser() {
        User advertiser = new User();
        User winner = new User();
        advertiser.setDisplayName("Advertiser");
        winner.setDisplayName("Winner");
        userRepository.save(advertiser);
        userRepository.save(winner);

        Listing card3DListing = listingRepository.findAll().get(0);
        card3DListing.setAdvertiser(advertiser);
        card3DListing.setWinner(winner);
        listingRepository.save(card3DListing);
    }

    @Test
    void checkListingByWinnerAndAdvertiserId() {
        createAndSetAdvertiserAndWinnerUser();

        Listing card3DListing = listingRepository.findAll().get(0);
        User advertiser = card3DListing.getAdvertiser();
        User winner = card3DListing.getWinner();

        Pageable pageable = Pageable.unpaged();

        assert listingRepository.findByAdvertiserId(advertiser.getId(), pageable).getTotalElements() == 1;
        assert listingRepository.findByWinnerId(winner.getId(), pageable).getTotalElements() == 1;
        assertEquals(advertiser.getDisplayName(), "Advertiser");
        assertEquals(winner.getDisplayName(), "Winner");
    }

    @Test
    void getWinnerAndAdvertiserFromListing() {
        createAndSetAdvertiserAndWinnerUser();

        Listing card3DListing = listingRepository.findAll().get(0);
        User advertiser = card3DListing.getAdvertiser();
        User winner = card3DListing.getWinner();

        Pageable pageable = Pageable.unpaged();

        assert listingRepository.findByAdvertiserId(advertiser.getId(), pageable).getTotalElements() == 1;
        assert listingRepository.findByWinnerId(winner.getId(), pageable).getTotalElements() == 1;
        assertEquals(advertiser.getDisplayName(), "Advertiser");
        assertEquals(winner.getDisplayName(), "Winner");
    }

    @Test
    @Transactional
    void addBidToListing(){
        User user = new User();
        user.setDisplayName("user");
        userRepository.save(user);

        Bid bid = new Bid();
        bid.setPrice(100);
        bid.setDescription("I will 3d print you a car");
        bid.setDaysToDeliver(7);
        bid.setBidder(user);

        Bid bid2 = new Bid();
        bid2.setPrice(100);
        bid2.setDescription("I will 3d print you a car");
        bid2.setDaysToDeliver(7);
        bid2.setBidder(user);

        Listing listing = new Listing();
        listing.setTitle("car 3d printing");
        listing.setDescription("I will 3d print you a car");
        listing.setEnded(false);
        listing.setCreationDate(new java.util.Date(System.currentTimeMillis()));
        listing.setExpirationDate(new java.util.Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
        listing.addPhoto("https://www.google.com");
        listing.setAdvertiser(userRepository.findAll().get(0));
        Listing after = listingRepository.save(listing);

        listing.addBid(bid);
        listing.addBid(bid2);
        listingRepository.save(listing);

        List<Bid> bids = after.getBids();
        List<Bid> allBids = bidRepository.findAll();

        assert bids.size() == 2;

    }


}
