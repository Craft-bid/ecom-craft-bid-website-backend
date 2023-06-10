package com.ecom.craftbid.controllers;

import com.ecom.craftbid.dtos.ListingDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.*;

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
    public ResponseEntity<Page<ListingDTO>> getAllListings(Specification<Listing> spec, Pageable pageable) {
        Page<Listing> listingPage = listingRepository.findAll(spec, pageable);
        Page<ListingDTO> listingDtoPage = listingPage.map(ListingDTO::fromListing);
        return ResponseEntity.ok(listingDtoPage);
    }

    @PostMapping("/private/listings")
    public ResponseEntity<ListingDTO> createListing(@RequestBody Listing listing) {
        Listing createdListing = listingRepository.save(listing);
        ListingDTO listingDto = ListingDTO.fromListing(createdListing);
        return ResponseEntity.ok(listingDto);
    }

    @GetMapping("/public/listings/{id}")
    @Transactional
    public ResponseEntity<ListingDTO> getListingById(@PathVariable long id) {
        Listing listing = listingRepository.findById(id);
        List<Listing> listings = listingRepository.findAll();
        ListingDTO listingDto = ListingDTO.fromListing(listing);
        return ResponseEntity.ok(listingDto);
    }

    @PutMapping("/private/listings/{id}")
    public ResponseEntity<ListingDTO> updateListing(@PathVariable long id, @RequestBody Listing updatedListing,
                                                    @RequestParam long winnerId, @RequestParam long advertiserId) {
        Listing existingListing = listingRepository.findById(id);

        existingListing.setTitle(updatedListing.getTitle());
        existingListing.setDescription(updatedListing.getDescription());

        Listing savedListing = listingRepository.save(existingListing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }

    @PutMapping("/private/listings/{id}/winner")
    public ResponseEntity<ListingDTO> updateListingWinner(@PathVariable long id, @RequestParam long winnerId) {
        try {
            Listing existingListing = listingRepository.findById(id);

            User winner = userRepository.findById(winnerId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
            existingListing.setWinner(winner);

            Listing savedListing = listingRepository.save(existingListing);
            ListingDTO listingDto = ListingDTO.fromListing(savedListing);
            return ResponseEntity.ok(listingDto);
        } catch (ChangeSetPersister.NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/private/listings/{id}/advertiser")
    public ResponseEntity<ListingDTO> updateListingAdvertiser(@PathVariable long id, @RequestParam long advertiserId) {
        try {
        Listing existingListing = listingRepository.findById(id);

        User advertiser = userRepository.findById(advertiserId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        existingListing.setAdvertiser(advertiser);

        Listing savedListing = listingRepository.save(existingListing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
        } catch (ChangeSetPersister.NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/private/listings/{id}/status")
    public ResponseEntity<ListingDTO> updateListingStatus(@PathVariable long id, @RequestParam Boolean ended) {
        Listing existingListing = listingRepository.findById(id);

        existingListing.setEnded(ended);

        Listing savedListing = listingRepository.save(existingListing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }

    @PutMapping("/private/listings/{id}/expirationDate")
    public ResponseEntity<ListingDTO> updateListingExpirationDate(@PathVariable long id, @RequestParam Date expirationDate) {
        Listing existingListing = listingRepository.findById(id);

        existingListing.setExpirationDate(expirationDate);

        Listing savedListing = listingRepository.save(existingListing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }

    @PutMapping("/private/listings/{id}/creationDate")
    public ResponseEntity<ListingDTO> updateListingCreationDate(@PathVariable long id, @RequestParam Date creationDate) {
        Listing existingListing = listingRepository.findById(id);

        existingListing.setCreationDate(creationDate);

        Listing savedListing = listingRepository.save(existingListing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/listings/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable long id) {
        listingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/private/{listingId}/tags")
    public ResponseEntity<ListingDTO> addTagsToListing(@PathVariable long listingId, @RequestBody List<Long> tagIds) {
        Listing listing = listingRepository.findById(listingId);

        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));

        listing.getTags().addAll(tags);

        Listing savedListing = listingRepository.save(listing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/{listingId}/tags/{tagId}")
    public ResponseEntity<ListingDTO> removeTagFromListing(@PathVariable long listingId, @PathVariable long tagId) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId);

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        listing.getTags().remove(tag);

        Listing savedListing = listingRepository.save(listing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }

    @PostMapping("/private/{listingId}/photos")
    public ResponseEntity<ListingDTO> addPhotosToListing(@PathVariable long listingId, @RequestBody List<String> photos) {
        Listing listing = listingRepository.findById(listingId);

        listing.getPhotos().addAll(photos);

        Listing savedListing = listingRepository.save(listing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/{listingId}/photos")
    public ResponseEntity<ListingDTO> removePhotoFromListing(@PathVariable long listingId, @RequestParam String photoPath) {
        Listing listing = listingRepository.findById(listingId);

        // Find the photo in the photos list by comparing the full path
        listing.getPhotos().removeIf(photo -> photo.equals(photoPath));

        Listing savedListing = listingRepository.save(listing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }

    @PostMapping("/private/{listingId}/bids")
    public ResponseEntity<ListingDTO> addBidToListing(@PathVariable long listingId, @RequestBody Bid bid) {
        Listing listing = listingRepository.findById(listingId);

        bid.setListing(listing);
        listing.getBids().add(bid);

        Listing savedListing = listingRepository.save(listing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/{listingId}/bids/{bidId}")
    public ResponseEntity<ListingDTO> removeBidFromListing(@PathVariable long listingId, @PathVariable long bidId) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId);

        Bid bidToRemove = listing.getBids().stream()
                .filter(bid -> bid.getId() == bidId)
                .findFirst()
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        listing.getBids().remove(bidToRemove);

        Listing savedListing = listingRepository.save(listing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }

    @PutMapping("/private/{listingId}/winner/{userId}")
    public ResponseEntity<ListingDTO> setWinnerForListing(@PathVariable long listingId, @PathVariable long userId) throws ChangeSetPersister.NotFoundException {
        Listing listing = listingRepository.findById(listingId);

        User winner = userRepository.findById(userId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        listing.setWinner(winner);

        Listing savedListing = listingRepository.save(listing);
        ListingDTO listingDto = ListingDTO.fromListing(savedListing);
        return ResponseEntity.ok(listingDto);
    }


    @GetMapping("/public/listings/active-listings")
    public ResponseEntity<Page<ListingDTO>> getActiveListingsSortedByExpirationDate(Pageable pageable) {
        Page<Listing> activeListings = listingRepository.findByEndedFalseOrderByExpirationDateDesc(pageable);
        Page<ListingDTO> listingDtoPage = activeListings.map(ListingDTO::fromListing);
        return ResponseEntity.ok(listingDtoPage);
    }

    @GetMapping("/public/listings/ended-listings")
    public ResponseEntity<Page<ListingDTO>> getEndedListingsSortedByExpirationDate(Pageable pageable) {
        Page<Listing> endedListings = listingRepository.findByEndedTrueOrderByExpirationDateDesc(pageable);
        Page<ListingDTO> listingDtoPage = endedListings.map(ListingDTO::fromListing);
        return ResponseEntity.ok(listingDtoPage);
    }

    @GetMapping("/public/listings/listings-by-title")
    public ResponseEntity<Page<ListingDTO>> getListingsByTitle(@RequestParam String title, Pageable pageable) {
        Page<Listing> listings = listingRepository.findByTitleContaining(title, pageable);
        Page<ListingDTO> listingDtoPage = listings.map(ListingDTO::fromListing);
        return ResponseEntity.ok(listingDtoPage);
    }

    @GetMapping("/public/listings/listings-by-tags")
    public ResponseEntity<Page<ListingDTO>> getListingsByTags(@RequestParam List<String> tags, Pageable pageable) {
        Page<Listing> listings = listingRepository.findByTags_NameIn(tags, pageable);
        Page<ListingDTO> listingDtoPage = listings.map(ListingDTO::fromListing);
        return ResponseEntity.ok(listingDtoPage);
    }

    @GetMapping("/public/listings/search")
    public ResponseEntity<Page<ListingDTO>> findBySearchCriteria(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String advertiserName,
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

        if (advertiserName != null && !advertiserName.isEmpty()) {
            spec = spec.or((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("advertiser").get("name")),
                            "%" + advertiserName.toLowerCase() + "%"));
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

        Page<Listing> searchResults = listingRepository.findAll(spec, pageable);
        Page<ListingDTO> listingDtoPage = searchResults.map(ListingDTO::fromListing);
        return ResponseEntity.ok(listingDtoPage);
    }


}

