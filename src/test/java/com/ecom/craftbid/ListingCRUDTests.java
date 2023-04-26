package com.ecom.craftbid;

import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@Slf4j
@SpringBootTest
public class ListingCRUDTests {
    /* if set to true it clears the whole database before each test */
    static final boolean DEBUG = true;

    @Autowired
    ListingRepository listingRepository;
    @Autowired
    TagRepository tagRepository;

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
                .photos(new ArrayList<>(Collections.singletonList("https://www.google.com")))
                .build();
        listingRepository.save(car3DListing);

        Tag car3DTag = Tag.builder().name("3d printing").build();
        List<Listing> list = new ArrayList<>(Collections.singletonList(car3DListing));
        car3DTag.setListings(list);
        tagRepository.save(car3DTag);

        car3DListing.setTags((Set<Tag>) new HashSet<>(Collections.singletonList(car3DTag)));
        listingRepository.save(car3DListing);

    }

    void cleanup() {
        listingRepository.deleteAll();
        tagRepository.deleteAll();
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

        assert(listingRepository.findByEndedTrue().size() == 0);
        assert(listingRepository.findByEndedFalse().size() == 1);

        card3DListing.setEnded(true);
        listingRepository.save(card3DListing);
        assert(listingRepository.findByEndedTrue().size() == 1);
        assert(listingRepository.findByEndedFalse().size() == 0);
    }



}
