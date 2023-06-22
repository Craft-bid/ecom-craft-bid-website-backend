package com.ecom.craftbid.controllers;

import com.ecom.craftbid.dtos.*;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.exceptions.UnauthorizedException;
import com.ecom.craftbid.services.BidService;
import com.ecom.craftbid.services.ListingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ListingController {

    private final ListingService listingService;

    private final BidService bidService;

    public ListingController(ListingService listingService, BidService bidService) {
        this.listingService = listingService;
        this.bidService = bidService;
    }

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            throw new UnauthorizedException("You must be logged in to create a listing");
        }
        listingCreateRequest.setAdvertiserId(((User)authentication.getPrincipal()).getId());

        ListingDTO listingDto = listingService.createListing(listingCreateRequest);
        return ResponseEntity.ok(listingDto);
    }

    @PatchMapping("/private/listings/{id}")
    public ResponseEntity<ListingDTO> patchListing(@PathVariable long id, @RequestBody ListingUpdateRequest updatedListing) {
        ListingDTO listingDto = listingService.patchListing(id, updatedListing);
        return ResponseEntity.ok(listingDto);
    }

    // TODO: probably not needed
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
    public ResponseEntity<ListingDTO> addTagsToListing(@PathVariable long listingId, @RequestBody List<TagDTO> tags) {
        ListingDTO listingDto = listingService.addTagsToListing(listingId, tags);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/{listingId}/tags/{tagId}")
    public ResponseEntity<ListingDTO> removeTagFromListing(@PathVariable long listingId, @PathVariable long tagId) {
        ListingDTO listingDto = listingService.removeTagFromListing(listingId, tagId);
        return ResponseEntity.ok(listingDto);
    }

    @PostMapping("/private/{listingId}/photos")
    public ResponseEntity<ListingDTO> addPhotosToListing(@PathVariable long listingId, @RequestParam("photos") MultipartFile[] photos) {
        ListingDTO listingDto = listingService.addPhotosToListing(listingId, photos);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/{listingId}/photos")
    public ResponseEntity<ListingDTO> removePhotoFromListing(@PathVariable long listingId, @RequestParam String photoPath) {
        ListingDTO listingDto = listingService.removePhotoFromListing(listingId, photoPath);
        return ResponseEntity.ok(listingDto);
    }

    @PostMapping("/private/{listingId}/bids")
    public ResponseEntity<ListingDTO> addBidToListing(@PathVariable long listingId, @RequestBody BidCreateRequest bidDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (listingService.isOwner(listingId, username))
            throw new UnauthorizedException("Owner cannot bid on his own listing");

        bidDto.setBidderUsername(username);
        ListingDTO listingDto = listingService.addBidToListing(bidDto, listingId);
        return ResponseEntity.ok(listingDto);
    }

    @DeleteMapping("/private/{listingId}/bids/{bidId}")
    public ResponseEntity<ListingDTO> removeBidFromListing(@PathVariable long listingId, @PathVariable long bidId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (!bidService.isBidOwner(bidId, username))
            throw new UnauthorizedException("Only the owner can modify bid");

        ListingDTO listingDto = listingService.removeBidFromListing(listingId, bidId);
        return ResponseEntity.ok(listingDto);
    }

    @PutMapping("/private/{listingId}/winner/{userId}")
    public ResponseEntity<ListingDTO> setWinnerForListing(@PathVariable long listingId, @PathVariable long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (!listingService.isOwner(listingId, username))
            throw new UnauthorizedException("Only the advertiser can modify a listing");

        ListingDTO listingDto = listingService.setWinnerForListing(listingId, userId);
        return ResponseEntity.ok(listingDto);
    }
}

