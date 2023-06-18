package com.ecom.craftbid.controllers;


import com.ecom.craftbid.dtos.BidCreateRequest;
import com.ecom.craftbid.dtos.BidDTO;
import com.ecom.craftbid.services.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BidController {

    @Autowired
    private BidService bidService;

    @GetMapping("/public/bids")
    public ResponseEntity<List<BidDTO>> getAllBids() {
        List<BidDTO> bids = bidService.getAllBids();
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/public/bids/{id}")
    public ResponseEntity<BidDTO> getBidById(@PathVariable long id) {
        BidDTO bid = bidService.getBidById(id);
        return ResponseEntity.ok(bid);
    }

    @PostMapping("/private/bids")
    public ResponseEntity<BidDTO> createBid(@RequestBody BidCreateRequest bid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        bid.setBidderUsername(currentUserName);

        BidDTO createdBid = bidService.createBid(bid);
        return ResponseEntity.ok(createdBid);
    }

    @DeleteMapping("/private/bids/{id}")
    public ResponseEntity<Void> deleteBid(@PathVariable long id) {
        bidService.deleteBid(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/bids/user/{userId}")
    public ResponseEntity<List<BidDTO>> getBidsByUser(@PathVariable long userId) {
        List<BidDTO> bids = bidService.getBidsByUser(userId);
        return ResponseEntity.ok(bids);
    }
}
