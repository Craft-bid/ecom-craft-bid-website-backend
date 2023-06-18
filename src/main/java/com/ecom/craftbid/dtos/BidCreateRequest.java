package com.ecom.craftbid.dtos;


import com.ecom.craftbid.entities.listing.Bid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidCreateRequest {
    private long price;
    private String description;
    private long daysToDeliver;
    private String bidderUsername;
    private long listingId;

    private Date creationDate = Calendar.getInstance().getTime();

    public static BidCreateRequest fromBid(Bid bid) {

        BidCreateRequest bidCreateRequest = new BidCreateRequest();
        bidCreateRequest.setPrice(bid.getPrice());
        bidCreateRequest.setDescription(bid.getDescription());
        bidCreateRequest.setCreationDate(bid.getCreationDate());
        bidCreateRequest.setDaysToDeliver(bid.getDaysToDeliver());
        bidCreateRequest.setBidderUsername(bid.getBidder().getUsername());
        return bidCreateRequest;
    }

    public Bid toBid() {
        Bid bid = new Bid();
        bid.setPrice(price);
        bid.setDescription(description);
        bid.setCreationDate(creationDate);
        bid.setDaysToDeliver(daysToDeliver);
        return bid;
    }
}
