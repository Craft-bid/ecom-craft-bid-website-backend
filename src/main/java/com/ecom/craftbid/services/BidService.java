package com.ecom.craftbid.services;

import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.exceptions.NotFoundException;
import com.ecom.craftbid.repositories.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    public Bid createBid(Bid bid) {
        return bidRepository.save(bid);
    }

    public Bid updateBid(long id, Bid updatedBid) {
        Bid bid = bidRepository.findById(id).orElseThrow(NotFoundException::new);

        bid.setPrice(updatedBid.getPrice());
        bid.setDescription(updatedBid.getDescription());
        bid.setCreationDate(updatedBid.getCreationDate());
        bid.setDaysToDeliver(updatedBid.getDaysToDeliver());
        bid.setBidder(updatedBid.getBidder());
        bid.setListing(updatedBid.getListing());
        return bidRepository.save(bid);

    }

    public void deleteBid(long id) {
        bidRepository.deleteById(id);
    }
}