package com.ecom.craftbid.controllers;


import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.services.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BidController {

    @Autowired
    private BidService bidService;

    @GetMapping("/public/bids")
    public ResponseEntity<List<Bid>> getAllBids() {
        List<Bid> bids = bidService.getAllBids();
        return ResponseEntity.ok(bids);
    }

    @PostMapping("/private/bids")
    public ResponseEntity<Bid> createBid(@RequestBody Bid bid) {
        Bid createdBid = bidService.createBid(bid);
        return ResponseEntity.ok(createdBid);
    }

    @PutMapping("/private/bids/{id}")
    public ResponseEntity<Bid> updateBid(@PathVariable long id, @RequestBody Bid updatedBid) {
        Bid updatedBidObj = bidService.updateBid(id, updatedBid);
        return ResponseEntity.ok(updatedBidObj);
    }

    @DeleteMapping("/private/bids/{id}")
    public ResponseEntity<Void> deleteBid(@PathVariable long id) {
        bidService.deleteBid(id);
        return ResponseEntity.noContent().build();
    }
}
