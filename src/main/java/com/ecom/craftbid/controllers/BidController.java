package com.ecom.craftbid.controllers;


import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.repositories.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BidController {

    @Autowired
    private BidRepository bidRepository;

    @GetMapping("/public/bids")
    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    @PostMapping("/private/bids")
    public Bid createBid(Bid bid) {
        return bidRepository.save(bid);
    }

    @PutMapping("/private/bids/{id}")
    public Bid updateBid(@PathVariable long id, @RequestBody Bid updatedBid) throws ChangeSetPersister.NotFoundException {
        Bid existingBid = bidRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);

        existingBid.setPrice(updatedBid.getPrice());
        existingBid.setDescription(updatedBid.getDescription());
        existingBid.setCreationDate(updatedBid.getCreationDate());
        existingBid.setDaysToDeliver(updatedBid.getDaysToDeliver());
        existingBid.setBidder(updatedBid.getBidder());
        existingBid.setListing(updatedBid.getListing());

        return bidRepository.save(existingBid);
    }

    @DeleteMapping("/private/bids/{id}")
    public void deleteBid(@PathVariable long id) {
        bidRepository.deleteById(id);
    }
}
