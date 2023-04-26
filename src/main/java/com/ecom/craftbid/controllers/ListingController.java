package com.ecom.craftbid.controllers;

import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.repositories.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingRepository listingRepository;

    @GetMapping
    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }
}
