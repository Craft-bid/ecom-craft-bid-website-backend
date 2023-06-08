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

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
public class ListingController {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/public/listings")
    public List<Listing> getAllListings(Specification<Listing> spec, Pageable pageable) {
        return listingRepository.findAll(spec, pageable).getContent();
    }

    @PostMapping("/private/listings")
    public Listing createListing(@RequestBody Listing listing) {
        return listingRepository.save(listing);
    }

    @GetMapping("/public/listings/{id}")
    public Listing getListingById(@PathVariable long id) {
        return listingRepository.findById(id);
    }

    @PutMapping("/private/listings/{id}")
    public Listing updateListing(@PathVariable long id, @RequestBody Listing updatedListing, @RequestParam long winnerId, @RequestParam long advertiserId) {
        Listing existingListing = listingRepository.findById(id);

        existingListing.setTitle(updatedListing.getTitle());
        existingListing.setDescription(updatedListing.getDescription());

        return listingRepository.save(existingListing);
    }

    @PutMapping("/private/listings/{id}/winner")
    public Listing updateListingWinner(@PathVariable long id, @RequestParam long winnerId) throws ChangeSetPersister.NotFoundException {
        Listing existingListing = listingRepository.findById(id);

        User winner = userRepository.findById(winnerId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        existingListing.setWinner(winner);

        return listingRepository.save(existingListing);
    }

    @PutMapping("/private/listings/{id}/advertiser")
    public Listing updateListingAdvertiser(@PathVariable long id, @RequestParam long advertiserId) throws ChangeSetPersister.NotFoundException {
        Listing existingListing = listingRepository.findById(id);

        User advertiser = userRepository.findById(advertiserId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        existingListing.setAdvertiser(advertiser);

        return listingRepository.save(existingListing);
    }

    @PutMapping("/private/listings/{id}/status")
    public Listing updateListingStatus(@PathVariable long id, @RequestParam Boolean ended) {
        Listing existingListing = listingRepository.findById(id);

        existingListing.setEnded(ended);

        return listingRepository.save(existingListing);
    }

    @PutMapping("/private/listings/{id}/expirationDate")
    public Listing updateListingExpirationDate(@PathVariable long id, @RequestParam Date expirationDate) {
        Listing existingListing = listingRepository.findById(id);

        existingListing.setExpirationDate(expirationDate);

        return listingRepository.save(existingListing);
    }

    @PutMapping("/private/listings/{id}/creationDate")
    public Listing updateListingCreationDate(@PathVariable long id, @RequestParam Date creationDate) {
        Listing existingListing = listingRepository.findById(id);

        existingListing.setCreationDate(creationDate);

        return listingRepository.save(existingListing);
    }

    @DeleteMapping("/private/listings/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable long id) {
        listingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/private/{listingId}/tags")
    public Listing addTagsToListing(@PathVariable long listingId, @RequestBody List<Long> tagIds) {
        Listing listing = listingRepository.findById(listingId);

        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));

        listing.getTags().addAll(tags);

        return listingRepository.save(listing);
    }

    @DeleteMapping("/private/{listingId}/tags/{tagId}")
    public Listing removeTagFromListing(@PathVariable long listingId, @PathVariable long tagId) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId);

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        listing.getTags().remove(tag);

        return listingRepository.save(listing);
    }

    @PostMapping("/private/{listingId}/photos")
    public Listing addPhotosToListing(@PathVariable long listingId, @RequestBody List<String> photos) {
        Listing listing = listingRepository.findById(listingId);

        listing.getPhotos().addAll(photos);

        return listingRepository.save(listing);
    }

    @DeleteMapping("/private/{listingId}/photos")
    public Listing removePhotoFromListing(@PathVariable long listingId, @RequestParam String photoPath) {
        Listing listing = listingRepository.findById(listingId);

        // Find the photo in the photos list by comparing the full path
        listing.getPhotos().removeIf(photo -> photo.equals(photoPath));

        return listingRepository.save(listing);
    }

    @PostMapping("/private/{listingId}/bids")
    public Listing addBidToListing(@PathVariable long listingId, @RequestBody Bid bid) {
        Listing listing = listingRepository.findById(listingId);

        bid.setListing(listing);
        listing.getBids().add(bid);

        return listingRepository.save(listing);
    }

    @DeleteMapping("/private/{listingId}/bids/{bidId}")
    public Listing removeBidFromListing(@PathVariable long listingId, @PathVariable long bidId) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId);

        Bid bidToRemove = listing.getBids().stream()
                .filter(bid -> bid.getId() == bidId)
                .findFirst()
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        listing.getBids().remove(bidToRemove);

        return listingRepository.save(listing);
    }

    @PutMapping("/private/{listingId}/winner/{userId}")
    public Listing setWinnerForListing(@PathVariable long listingId, @PathVariable long userId) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId);

        User winner = userRepository.findById(userId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);


        listing.setWinner(winner);

        return listingRepository.save(listing);
    }

    @GetMapping("/public/listings/active-listings")
    public Page<Listing> getActiveListingsSortedByExpirationDate(Pageable pageable) {
        return listingRepository.findByEndedFalseOrderByExpirationDateDesc(pageable);
    }

    @GetMapping("/public/listings/ended-listings")
    public Page<Listing> getEndedListingsSortedByExpirationDate(Pageable pageable) {
        return listingRepository.findByEndedTrueOrderByExpirationDateDesc(pageable);
    }

    @GetMapping("/public/listings/listings-by-title")
    public Page<Listing> getListingsByTitle(@RequestParam String title, Pageable pageable) {
        return listingRepository.findByTitleContaining(title, pageable);
    }

    @GetMapping("/public/listings/listings-by-tags")
    public Page<Listing> getListingsByTags(@RequestParam List<String> tags, Pageable pageable) {
        return listingRepository.findByTags_NameIn(tags, pageable);
    }

    @GetMapping("/public/listings/search")
    public Page<Listing> findBySearchCriteria(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String advertiserSurname,
            @RequestParam(required = false) String winnerName,
            @RequestParam(required = false) List<String> tagNames,
            @RequestParam(required = false) Date dateFrom,
            @RequestParam(required = false) Date dateTo,
            Pageable pageable) {

        Specification<Listing> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.or((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (advertiserSurname != null && !advertiserSurname.isEmpty()) {
            spec = spec.or((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("advertiser").get("name")),
                            "%" + advertiserSurname.toLowerCase() + "%"));
        }

        if (winnerName != null && !winnerName.isEmpty()) {
            spec = spec.or((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("winner").get("name")),
                            "%" + winnerName.toLowerCase() + "%"));
        }

        if (tagNames != null && !tagNames.isEmpty()) {
            spec = spec.or((root, query, criteriaBuilder) ->
                    root.join("tags").get("name").in(tagNames));
        }

        if (dateFrom != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("creationDate"), dateFrom)
            );
        }

        if (dateTo != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("expirationDate"), dateTo)
            );

        }

        return listingRepository.findAll(spec, pageable);
    }

}

