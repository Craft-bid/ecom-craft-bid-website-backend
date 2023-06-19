package com.ecom.craftbid.services;

import com.ecom.craftbid.dtos.BidCreateRequest;
import com.ecom.craftbid.dtos.BidDTO;
import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.exceptions.NotFoundException;
import com.ecom.craftbid.repositories.BidRepository;
import com.ecom.craftbid.repositories.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserService userService;

    public List<BidDTO> getAllBids() {
        return BidDTO.fromBids(bidRepository.findAll());
    }

    public BidDTO getBidById(long id) {
        Bid bid = bidRepository.findById(id).orElseThrow(NotFoundException::new);
        return BidDTO.fromBid(bid);
    }

    public boolean isBidOwner(long bidId, String username) {
        Bid bid = bidRepository.findById(bidId).orElseThrow(NotFoundException::new);
        return bid.getBidder().getId() == userService.findUserByEmail(username).getId();
    }

    public BidDTO createBid(BidCreateRequest bidRequest) {
        Bid bid = new Bid();
        bid.setPrice(bidRequest.getPrice());
        bid.setDescription(bidRequest.getDescription());
        bid.setCreationDate(bidRequest.getCreationDate());
        bid.setDaysToDeliver(bidRequest.getDaysToDeliver());
        bid.setBidder(userService.findUserByEmail(bidRequest.getBidderUsername()));
        bid.setListing(listingRepository.findById(bidRequest.getListingId()).orElseThrow(NotFoundException::new));

        Bid save = bidRepository.save(bid);
        return BidDTO.fromBid(save);
    }

    public List<BidDTO> getBidsByUser(long userId) {
        return BidDTO.fromBids(bidRepository.findByBidderId(userId));
    }

    public void deleteBid(long id) {
        bidRepository.deleteById(id);
    }
}