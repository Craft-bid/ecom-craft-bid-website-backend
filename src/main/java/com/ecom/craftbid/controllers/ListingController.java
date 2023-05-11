package com.ecom.craftbid.controllers;

import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.repositories.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public Listing createListing(@RequestBody Listing listing) {
        return listingRepository.save(listing);
    }

    @PutMapping("/{id}")
    public Listing updateListing(@PathVariable long id, @RequestBody Listing updatedListing) throws ChangeSetPersister.NotFoundException {
        Listing existingListing = listingRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        existingListing.setTitle(updatedListing.getTitle());
        existingListing.setDescription(updatedListing.getDescription());

        return listingRepository.save(existingListing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable long id) {
        listingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

