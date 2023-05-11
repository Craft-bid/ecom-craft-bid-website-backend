package com.ecom.craftbid.controllers;

import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private TagRepository tagRepository;

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

    @PostMapping("/{listingId}/tags")
    public Listing addTagsToListing(@PathVariable long listingId, @RequestBody List<Long> tagIds) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));

        listing.getTags().addAll(tags);

        return listingRepository.save(listing);
    }

    @DeleteMapping("/{listingId}/tags/{tagId}")
    public Listing removeTagFromListing(@PathVariable long listingId, @PathVariable long tagId) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        listing.getTags().remove(tag);

        return listingRepository.save(listing);
    }
}

