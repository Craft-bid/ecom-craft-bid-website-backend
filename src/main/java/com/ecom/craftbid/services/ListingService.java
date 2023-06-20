package com.ecom.craftbid.services;

import com.ecom.craftbid.dtos.*;
import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.exceptions.NotFoundException;
import com.ecom.craftbid.repositories.BidRepository;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.TagRepository;
import com.ecom.craftbid.repositories.UserRepository;
import com.ecom.craftbid.utils.PhotosManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BidService bidService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    public boolean isOwner(long listingId, String email) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(NotFoundException::new);
        return listing.getAdvertiser().getId() == userService.findUserByEmail(email).getId();
    }

    private Listing findListingById(long listingId) throws NotFoundException {
        return listingRepository.findById(listingId)
                .orElseThrow(NotFoundException::new);
    }

    public Page<ListingDTO> getAllListings(Specification<Listing> spec, Pageable pageable) {
        Page<Listing> listingPage = listingRepository.findAll(spec, pageable);
        return listingPage.map(ListingDTO::fromListing);
    }

    public ListingDTO createListing(ListingCreateRequest listingCreateRequest) {
        Listing createdListing = new Listing();
        createdListing.setTitle(listingCreateRequest.getTitle());
        createdListing.setDescription(listingCreateRequest.getDescription());

        User advertiser = userService.findUserById(listingCreateRequest.getAdvertiserId());

        createdListing.setAdvertiser(advertiser);
        createdListing.setEnded(listingCreateRequest.getEnded());

        listingRepository.save(createdListing);
        return ListingDTO.fromListing(createdListing);
    }

    public UserDTO getUserById(long userId) throws NotFoundException {
        return UserDTO.fromUser(userService.findUserById(userId));
    }

    public ListingDTO getListingById(long id) {
        Listing listing = listingRepository.findById(id).orElseThrow(NotFoundException::new);
        return ListingDTO.fromListing(listing);
    }

    public ListingDTO updateListing(long id, Listing updatedListing, long winnerId, long advertiserId) throws NotFoundException {
        Listing listing = findListingById(id);
        User winner = userService.findUserById(winnerId);
        User advertiser = userService.findUserById(advertiserId);

        listing.setTitle(updatedListing.getTitle());
        listing.setDescription(updatedListing.getDescription());
        listing.setWinner(winner);
        listing.setAdvertiser(advertiser);

        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }

    public ListingDTO updateListingWinner(long id, long winnerId) throws NotFoundException {
        Listing listing = findListingById(id);
        User winner = userService.findUserById(winnerId);

        listing.setWinner(winner);
        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }

    public ListingDTO updateListingAdvertiser(long id, long advertiserId) throws NotFoundException {
        Listing listing = findListingById(id);
        User advertiser = userService.findUserById(advertiserId);
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

    public ListingDTO addTagsToListing(long listingId, List<TagDTO> tags) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(NotFoundException::new);
        for (TagDTO tagDTO : tags) {
            listing.addTag(TagDTO.toTag(tagDTO));
        }
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

    public ListingDTO addPhotosToListing(long listingId, MultipartFile[] photos) {
        Listing listing = findListingById(listingId);

        List<String> addedPhotosPaths = PhotosManager.saveFiles(photos, listing.getId());
        listing.getPhotos().addAll(addedPhotosPaths);

        return saveAndReturnListingDTO(listing);
    }

    public ListingDTO removePhotoFromListing(long listingId, String photoPath) {
        Listing listing = findListingById(listingId);
        listing.getPhotos().removeIf(photo -> photo.equals(photoPath));
        return saveAndReturnListingDTO(listing);
    }

    public ListingDTO addBidToListing(BidCreateRequest bidCreateRequest, long listingId) {
        Listing listing = findListingById(listingId);
        User bidder = userService.findUserByEmail(bidCreateRequest.getBidderUsername());
        Bid bid = bidCreateRequest.toBid();
        bid.setBidder(bidder);
        listing.addBid(bid);
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

        User winner = userService.findUserById(userId);
        if (listing.getBids().stream().noneMatch(bid -> bid.getBidder().getId() == userId)) {
            throw new NotFoundException("User with ID " + userId + " is not a bidder for listing with ID " + listingId);
        }

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

    public Page<ListingDTO> findAllAdmin(Pageable pageable) {
        Page<Listing> listings = listingRepository.findAll(pageable);
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
        Double minPrice = searchRequest.getMinPrice();
        Double maxPrice = searchRequest.getMaxPrice();

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

        if (minPrice != null && maxPrice != null && minPrice <= maxPrice && minPrice != 0 && maxPrice != 0) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Subquery<Double> subquery = query.subquery(Double.class);
                filterByPriceConstruct(root, criteriaBuilder, subquery);

                return criteriaBuilder.between(subquery.getSelection(), minPrice, maxPrice);
            });
        } else if (minPrice != null && minPrice >= 0) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Subquery<Double> subquery = query.subquery(Double.class);
                filterByPriceConstruct(root, criteriaBuilder, subquery);

                return criteriaBuilder.greaterThanOrEqualTo(subquery.getSelection(), minPrice);
            });
        } else if (maxPrice != null && maxPrice != 0) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Subquery<Double> subquery = query.subquery(Double.class);
                filterByPriceConstruct(root, criteriaBuilder, subquery);

                return criteriaBuilder.lessThanOrEqualTo(subquery.getSelection(), maxPrice);
            });
        }

        Page<Listing> searchResults = listingRepository.findAll(spec, pageable);
        return searchResults.map(ListingDTO::fromListing);
    }

    private static void filterByPriceConstruct(Root<Listing> root, CriteriaBuilder criteriaBuilder, Subquery<Double> subquery) {
        Root<Listing> subqueryRoot = subquery.from(Listing.class);
        Join<Listing, Bid> bidJoin = subqueryRoot.join("bids");
        Expression<Double> averagePriceExpression = criteriaBuilder.avg(bidJoin.get("price"));
        subquery.select(averagePriceExpression)
                .where(criteriaBuilder.equal(subqueryRoot, root))
                .groupBy(subqueryRoot);
    }


    private ListingDTO saveAndReturnListingDTO(Listing listing) {
        Listing savedListing = listingRepository.save(listing);
        return ListingDTO.fromListing(savedListing);
    }


    public ListingDTO patchListing(long id, ListingUpdateRequest updatedListing) {
        Listing listing = findListingById(id);

        if (updatedListing.getTitle() != null) {
            listing.setTitle(updatedListing.getTitle());
        }
        if (updatedListing.getEnded() != null) {
            listing.setEnded(updatedListing.getEnded());
        }
        if (updatedListing.getExpirationDate() != null) {
            listing.setExpirationDate(updatedListing.getExpirationDate());
        }
        if (updatedListing.getCreationDate() != null) {
            listing.setCreationDate(updatedListing.getCreationDate());
        }
        if (updatedListing.getDescription() != null) {
            listing.setDescription(updatedListing.getDescription());
        }
        if (updatedListing.getAdvertiserId() > 0) {
            listing.setAdvertiser(userService.findUserById(updatedListing.getAdvertiserId()));
        }
        if (updatedListing.getWinnerId() > 0) {
            listing.setWinner(userService.findUserById(updatedListing.getWinnerId()));
        }

        listingRepository.flush();

        return ListingDTO.fromListing(listing);
    }

    public List<ListingDTO> getListingsPage(Pageable pageable) {
        Page<Listing> listings = listingRepository.findAll(pageable);

        return listings.stream().map(ListingDTO::fromListing).toList();
    }
}
