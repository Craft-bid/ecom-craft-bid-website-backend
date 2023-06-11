package com.ecom.craftbid.controllers;

import com.ecom.craftbid.dtos.ListingCreateRequest;
import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.dtos.ListingUpdateRequest;
import com.ecom.craftbid.dtos.SearchCriteriaDto;
import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.services.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @GetMapping("/public/listings/{id}")
    public ResponseEntity<ListingDTO> getListingById(@PathVariable long id) {
        ListingDTO listingDto = listingService.getListingById(id);
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

    @GetMapping("/public/listings-by-tags")
    public ResponseEntity<Page<ListingDTO>> getListingsByTags(@RequestParam List<String> tags, Pageable pageable) {
        Page<ListingDTO> listingDtoPage = listingService.getListingsByTags(tags, pageable);
        return ResponseEntity.ok(listingDtoPage);
    }

    @GetMapping("/public/listings/search")
    public ResponseEntity<List<ListingDTO>> findBySearchCriteria(@ModelAttribute SearchCriteriaDto searchCriteriaDto, Pageable pageable) {
        List<ListingDTO> listingDtoPage = listingService.findBySearchCriteria(searchCriteriaDto, pageable);
        return ResponseEntity.ok(listingDtoPage);
    }

    @PostMapping("/private/listings")
    public ResponseEntity<ListingDTO> createListing(@RequestBody ListingCreateRequest listingCreateRequest) {
        ListingDTO listingDto = listingService.createListing(listingCreateRequest);
        return ResponseEntity.ok(listingDto);
    }

    @PatchMapping("/private/listings/{id}")
    public ResponseEntity<ListingDTO> patchListing(@PathVariable long id, @RequestBody ListingUpdateRequest updatedListing) {
        ListingDTO listingDto = listingService.patchListing(id, updatedListing);
        return ResponseEntity.ok(listingDto);
    }

    @PutMapping("/private/listings/{id}")
    public ResponseEntity<ListingDTO> updateListing(@PathVariable long id, @RequestBody Listing updatedListing, @RequestParam long winnerId, @RequestParam long advertiserId) {
        ListingDTO listingDto = listingService.updateListing(id, updatedListing, winnerId, advertiserId);
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
}

