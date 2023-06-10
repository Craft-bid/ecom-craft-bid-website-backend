package com.ecom.craftbid.services;

import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.dtos.SearchCriteriaDto;
import com.ecom.craftbid.dtos.UserDTO;
import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.exceptions.NotFoundException;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.TagRepository;
import com.ecom.craftbid.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;



    private Listing findListingById(long listingId) throws NotFoundException {
        return listingRepository.findById(listingId)
                .orElseThrow(NotFoundException::new);
    }

    private User findUserById(long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
    }

    public Page<ListingDTO> getAllListings(Specification<Listing> spec, Pageable pageable) {
        Page<Listing> listingPage = listingRepository.findAll(spec, pageable);
        return listingPage.map(ListingDTO::fromListing);
    }

    public ListingDTO createListing(Listing listing) {
        Listing createdListing = listingRepository.save(listing);
        return ListingDTO.fromListing(createdListing);
    }

    public UserDTO getUserById(long userId) throws NotFoundException {
        return UserDTO.fromUser(findUserById(userId));
    }
    public ListingDTO getListingById(long id) {
        Listing listing = listingRepository.findById(id).orElseThrow(NotFoundException::new);
        return ListingDTO.fromListing(listing);
    }
    public ListingDTO updateListing(long id, Listing updatedListing, long winnerId, long advertiserId) throws NotFoundException {
        Listing listing = findListingById(id);
        User winner = findUserById(winnerId);
        User advertiser = findUserById(advertiserId);

        listing.setTitle(updatedListing.getTitle());
        listing.setDescription(updatedListing.getDescription());
        listing.setWinner(winner);
        listing.setAdvertiser(advertiser);

        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }

    public ListingDTO updateListingWinner(long id, long winnerId) throws NotFoundException {
        Listing listing = findListingById(id);
        User winner = findUserById(winnerId);

        listing.setWinner(winner);
        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }

    public ListingDTO updateListingAdvertiser(long id, long advertiserId) throws NotFoundException {
        Listing listing = findListingById(id);
        User advertiser = findUserById(advertiserId);
        if (advertiser == null) {
            throw new NotFoundException("Advertiser with ID " + advertiserId + " not found.");
        }
        listing.setAdvertiser(advertiser);
        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }

    public ListingDTO updateListingStatus(long id, boolean ended) {
        Listing listing = findListingById(id);
        listing.setEnded(ended);
        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }

    public ListingDTO updateListingExpirationDate(long id, Date expirationDate) {
        Listing listing = findListingById(id);
        listing.setExpirationDate(expirationDate);
        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }

    public ListingDTO updateListingCreationDate(long id, Date creationDate) {
        Listing listing = findListingById(id);
        listing.setCreationDate(creationDate);
        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }

    public void deleteListing(long id) {
        Listing listing = findListingById(id);
        listingRepository.delete(listing);
    }

    public ListingDTO addTagsToListing(long listingId, List<Long> tagIds) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(NotFoundException::new);
        List<Tag> tags = tagRepository.findAllById(tagIds);
        listing.getTags().addAll(tags);
        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }

    public ListingDTO removeTagFromListing(long listingId, long tagId) {
        Listing listing = findListingById(listingId);
        Tag tag = tagRepository.findById(tagId).orElseThrow(NotFoundException::new);
        listing.getTags().remove(tag);
        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }

    public ListingDTO addPhotosToListing(long listingId, List<String> photos) {
        Listing listing = findListingById(listingId);
        listing.getPhotos().addAll(photos);
        return saveAndReturnListingDTO(listing);
    }

    public ListingDTO removePhotoFromListing(long listingId, String photoPath) {
        Listing listing = findListingById(listingId);
        listing.getPhotos().removeIf(photo -> photo.equals(photoPath));
        return saveAndReturnListingDTO(listing);
    }

    public ListingDTO addBidToListing(long listingId, Bid bid) {
        Listing listing = findListingById(listingId);
        bid.setListing(listing);
        listing.getBids().add(bid);
        return saveAndReturnListingDTO(listing);
    }

    public ListingDTO removeBidFromListing(long listingId, long bidId) {
        Listing listing = findListingById(listingId);
        Bid bidToRemove = listing.getBids().stream()
                .filter(bid -> bid.getId() == bidId)
                .findFirst()
                .orElseThrow(NotFoundException::new);
        listing.getBids().remove(bidToRemove);
        return saveAndReturnListingDTO(listing);
    }

    public ListingDTO setWinnerForListing(long listingId, long userId) {
        Listing listing = findListingById(listingId);
        User winner = userRepository.findById(userId)
                .orElseThrow();
        listing.setWinner(winner);
        return saveAndReturnListingDTO(listing);
    }

    public Page<ListingDTO> getActiveListingsSortedByExpirationDate(Pageable pageable) {
        Page<Listing> activeListings = listingRepository.findByEndedFalseOrderByExpirationDateDesc(pageable);
        return activeListings.map(ListingDTO::fromListing);
    }

    public Page<ListingDTO> getEndedListingsSortedByExpirationDate(Pageable pageable) {
        Page<Listing> endedListings = listingRepository.findByEndedTrueOrderByExpirationDateDesc(pageable);
        return endedListings.map(ListingDTO::fromListing);
    }

    public Page<ListingDTO> getListingsByTitle(String title, Pageable pageable) {
        Page<Listing> listings = listingRepository.findByTitleContaining(title, pageable);
        return listings.map(ListingDTO::fromListing);
    }

    public Page<ListingDTO> getListingsByTags(List<String> tags, Pageable pageable) {
        Page<Listing> listings = listingRepository.findByTags_NameIn(tags, pageable);
        return listings.map(ListingDTO::fromListing);
    }

    public Page<ListingDTO> findBySearchCriteria(@ModelAttribute SearchCriteriaDto searchRequest, Pageable pageable) {
        Specification<Listing> spec = Specification.where(null);

        String title = searchRequest.getTitle();
        String advertiserName = searchRequest.getAdvertiserName();
        String winnerName = searchRequest.getWinnerName();
        List<String> tagNames = searchRequest.getTagNames();
        Date dateFrom = searchRequest.getDateFrom();
        Date dateTo = searchRequest.getDateTo();
        
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
        return searchResults.map(ListingDTO::fromListing);
    }


    private ListingDTO saveAndReturnListingDTO(Listing listing) {
        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }


}
