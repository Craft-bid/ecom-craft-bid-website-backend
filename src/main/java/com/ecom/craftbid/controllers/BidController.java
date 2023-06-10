package com.ecom.craftbid.controllers;


import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.repositories.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BidController {

    @Autowired
    private BidRepository bidRepository;

    @GetMapping("/public/bids")
    public ResponseEntity<List<Bid>> getAllBids() {
        List<Bid> bids = bidRepository.findAll();
        return ResponseEntity.ok(bids);
    }

    @PostMapping("/private/bids")
    public ResponseEntity<Bid> createBid(@RequestBody Bid bid) {
        Bid createdBid = bidRepository.save(bid);
        return ResponseEntity.ok(createdBid);
    }

    @PutMapping("/private/bids/{id}")
    public ResponseEntity<Bid> updateBid(@PathVariable long id, @RequestBody Bid updatedBid) {
        try {
            Bid existingBid = bidRepository.findById(id)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            existingBid.setPrice(updatedBid.getPrice());
            existingBid.setDescription(updatedBid.getDescription());
            existingBid.setCreationDate(updatedBid.getCreationDate());
            existingBid.setDaysToDeliver(updatedBid.getDaysToDeliver());
            existingBid.setBidder(updatedBid.getBidder());
            existingBid.setListing(updatedBid.getListing());

            Bid updatedBidObj = bidRepository.save(existingBid);
            return ResponseEntity.ok(updatedBidObj);
        } catch (ChangeSetPersister.NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/private/bids/{id}")
    public ResponseEntity<Void> deleteBid(@PathVariable long id) {
        bidRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
