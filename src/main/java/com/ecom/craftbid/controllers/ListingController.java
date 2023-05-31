package com.ecom.craftbid.controllers;

import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.TagRepository;
import com.ecom.craftbid.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;

import org.springframework.data.querydsl.QPageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
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

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Listing> getAllListings(Pageable pageable) {
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

    @PostMapping("/{listingId}/photos")
    public Listing addPhotosToListing(@PathVariable long listingId, @RequestBody List<String> photos) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        listing.getPhotos().addAll(photos);

        return listingRepository.save(listing);
    }

    @DeleteMapping("/{listingId}/photos/{photo}")
    public Listing removePhotoFromListing(@PathVariable long listingId, @PathVariable String photo) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        listing.getPhotos().remove(photo);

        return listingRepository.save(listing);
    }

    @PostMapping("/{listingId}/bids")
    public Listing addBidToListing(@PathVariable long listingId, @RequestBody Bid bid) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        bid.setListing(listing);
        listing.getBids().add(bid);

        return listingRepository.save(listing);
    }

    @DeleteMapping("/{listingId}/bids/{bidId}")
    public Listing removeBidFromListing(@PathVariable long listingId, @PathVariable long bidId) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        Bid bidToRemove = listing.getBids().stream()
                .filter(bid -> bid.getId() == bidId)
                .findFirst()
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        listing.getBids().remove(bidToRemove);

        return listingRepository.save(listing);
    }

    @PutMapping("/{listingId}/winner/{userId}")
    public Listing setWinnerForListing(@PathVariable long listingId, @PathVariable long userId) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        User winner = userRepository.findById(userId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);


        listing.setWinner(winner);

        return listingRepository.save(listing);
    }

    @GetMapping("/active-listings")
    public Page<Listing> getActiveListingsSortedByExpirationDate(Pageable pageable) {
        return listingRepository.findByEndedFalseOrderByExpirationDateDesc(pageable);
    }

    @GetMapping("/ended-listings")
    public Page<Listing> getEndedListingsSortedByExpirationDate(Pageable pageable) {
        return listingRepository.findByEndedTrueOrderByExpirationDateDesc(pageable);
    }

    @GetMapping("/listings-by-title")
    public Page<Listing> getListingsByTitle(@RequestParam String title, Pageable pageable) {
        return listingRepository.findByTitleContaining(title, pageable);
    }

    @GetMapping("/listings-by-tag")
    public Page<Listing> getListingsByTag(@RequestParam String tag, Pageable pageable) {
        return listingRepository.findByTags_Name(tag, pageable);
    }


}

