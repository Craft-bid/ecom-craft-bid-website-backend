package com.ecom.craftbid.dtos;


import com.ecom.craftbid.entities.listing.Bid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidCreateRequest {
    private long price;
    private String description;
    private long daysToDeliver;
    private long bidderId;
    private long listingId;

    private Date creationDate = Calendar.getInstance().getTime();

    public static BidCreateRequest fromBid(Bid bid) {
        return BidCreateRequest.builder()
                .price(bid.getPrice())
                .description(bid.getDescription())
                .creationDate(bid.getCreationDate())
                .daysToDeliver(bid.getDaysToDeliver())
                .bidderId(bid.getBidder().getId())
                .build();
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
