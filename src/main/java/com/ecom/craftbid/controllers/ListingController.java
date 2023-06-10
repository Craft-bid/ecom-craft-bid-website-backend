package com.ecom.craftbid.controllers;

import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.exception.NotFoundException;
import com.ecom.craftbid.services.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @GetMapping("/public/listings")
    public ResponseEntity<Page<ListingDTO>> getAllListings(Specification<Listing> spec, Pageable pageable) {
        Page<ListingDTO> listingDtoPage = listingService.getAllListings(spec, pageable);
        return ResponseEntity.ok(listingDtoPage);
    }

    @PostMapping("/private/listings")
    public ResponseEntity<ListingDTO> createListing(@RequestBody Listing listing) {
        ListingDTO listingDto = listingService.createListing(listing);
        return ResponseEntity.ok(listingDto);
    }

    @GetMapping("/public/listings/{id}")
    public ResponseEntity<ListingDTO> getListingById(@PathVariable long id) {
        try {
            ListingDTO listingDto = listingService.getListingById(id);
            return ResponseEntity.ok(listingDto);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/private/listings/{id}")
    public ResponseEntity<ListingDTO> updateListing(@PathVariable long id, @RequestBody Listing updatedListing,
                                                    @RequestParam long winnerId, @RequestParam long advertiserId) {
        try {
            ListingDTO listingDto = listingService.updateListing(id, updatedListing, winnerId, advertiserId);
            return ResponseEntity.ok(listingDto);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/private/listings/{id}/winner")
    public ResponseEntity<ListingDTO> updateListingWinner(@PathVariable long id, @RequestParam long winnerId) {
        try {
            ListingDTO listingDto = listingService.updateListingWinner(id, winnerId);
            return ResponseEntity.ok(listingDto);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/private/listings/{id}/advertiser")
    public ResponseEntity<ListingDTO> updateListingAdvertiser(@PathVariable long id, @RequestParam long advertiserId) {
        try {
            ListingDTO listingDto = listingService.updateListingAdvertiser(id, advertiserId);
            return ResponseEntity.ok(listingDto);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/private/listings/{id}/status")
    public ResponseEntity<ListingDTO> updateListingStatus(@PathVariable long id, @RequestParam Boolean ended) {
        ListingDTO listingDto = listingService.updateListingStatus(id, ended);
        return ResponseEntity.ok(listingDto);
    }

    @PutMapping("/private/listings/{id}/expirationDate")
    public ResponseEntity<ListingDTO> updateListingExpirationDate(@PathVariable long id, @RequestParam Date expirationDate) {
        ListingDTO listingDto = listingService.updateListingExpirationDate(id, expirationDate);
        return ResponseEntity.ok(listingDto);
    }

    @PutMapping("/private/listings/{id}/creationDate")
    public ResponseEntity<ListingDTO> updateListingCreationDate(@PathVariable long id, @RequestParam Date creationDate) {
        ListingDTO listingDto = listingService.updateListingCreationDate(id, creationDate);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/listings/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable long id) {
        listingService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/private/{listingId}/tags")
    public ResponseEntity<ListingDTO> addTagsToListing(@PathVariable long listingId, @RequestBody List<Long> tagIds) {
        ListingDTO listingDto = listingService.addTagsToListing(listingId, tagIds);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/{listingId}/tags/{tagId}")
    public ResponseEntity<ListingDTO> removeTagFromListing(@PathVariable long listingId, @PathVariable long tagId) {
        ListingDTO listingDto = listingService.removeTagFromListing(listingId, tagId);
        return ResponseEntity.ok(listingDto);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
    @PostMapping("/private/{listingId}/photos")
    public ResponseEntity<ListingDTO> addPhotosToListing(@PathVariable long listingId, @RequestBody List<String> photos) {
        ListingDTO listingDto = listingService.addPhotosToListing(listingId, photos);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/{listingId}/photos")
    public ResponseEntity<ListingDTO> removePhotoFromListing(@PathVariable long listingId, @RequestParam String photoPath) {
        ListingDTO listingDto = listingService.removePhotoFromListing(listingId, photoPath);
        return ResponseEntity.ok(listingDto);
    }

    @PostMapping("/private/{listingId}/bids")
    public ResponseEntity<ListingDTO> addBidToListing(@PathVariable long listingId, @RequestBody Bid bid) {
        ListingDTO listingDto = listingService.addBidToListing(listingId, bid);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/{listingId}/bids/{bidId}")
    public ResponseEntity<ListingDTO> removeBidFromListing(@PathVariable long listingId, @PathVariable long bidId) {
        ListingDTO listingDto = listingService.removeBidFromListing(listingId, bidId);
        return ResponseEntity.ok(listingDto);
    }

    @PutMapping("/private/{listingId}/winner/{userId}")
    public ResponseEntity<ListingDTO> setWinnerForListing(@PathVariable long listingId, @PathVariable long userId) {
        ListingDTO listingDto = listingService.setWinnerForListing(listingId, userId);
        return ResponseEntity.ok(listingDto);
    }

    @GetMapping("/public/active-listings")
    public ResponseEntity<Page<ListingDTO>> getActiveListingsSortedByExpirationDate(Pageable pageable) {
        Page<ListingDTO> listingDtoPage = listingService.getActiveListingsSortedByExpirationDate(pageable);
        return ResponseEntity.ok(listingDtoPage);
    }

    @GetMapping("/public/ended-listings")
    public ResponseEntity<Page<ListingDTO>> getEndedListingsSortedByExpirationDate(Pageable pageable) {
        Page<ListingDTO> listingDtoPage = listingService.getEndedListingsSortedByExpirationDate(pageable);
        return ResponseEntity.ok(listingDtoPage);
    }

    @GetMapping("/public/listings-by-title")
    public ResponseEntity<Page<ListingDTO>> getListingsByTitle(@RequestParam String title, Pageable pageable) {
        Page<ListingDTO> listingDtoPage = listingService.getListingsByTitle(title, pageable);
        return ResponseEntity.ok(listingDtoPage);
    }

    @GetMapping("/public/listings-by-tags")
    public ResponseEntity<Page<ListingDTO>> getListingsByTags(@RequestParam List<String> tags, Pageable pageable) {
        Page<ListingDTO> listingDtoPage = listingService.getListingsByTags(tags, pageable);
        return ResponseEntity.ok(listingDtoPage);
    }

    @GetMapping("/public/search")
    public ResponseEntity<Page<ListingDTO>> findBySearchCriteria(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String advertiserName,
            @RequestParam(required = false) String winnerName,
            @RequestParam(required = false) List<String> tagNames,
            @RequestParam(required = false) Date dateFrom,
            @RequestParam(required = false) Date dateTo,
            Pageable pageable) {
        Page<ListingDTO> listingDtoPage = listingService.findBySearchCriteria(
                title, advertiserName, winnerName, tagNames, dateFrom, dateTo, pageable);
        return ResponseEntity.ok(listingDtoPage);
    }


}

