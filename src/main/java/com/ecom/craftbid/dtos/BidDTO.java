package com.ecom.craftbid.dtos;

import com.ecom.craftbid.entities.listing.Bid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BidDTO {
    private long id;
    private long price;
    private String description;
    private Date creationDate;
    private long daysToDeliver;
    private String bidderName;
    private long listingId;
    private long bidderId;

    public static BidDTO fromBid(Bid bid) {
        return BidDTO.builder()
                .id(bid.getId())
                .price(bid.getPrice())
                .description(bid.getDescription())
                .creationDate(bid.getCreationDate())
                .daysToDeliver(bid.getDaysToDeliver())
                .bidderName(bid.getBidder().getDisplayName())
                .listingId(bid.getListing().getId())
                .bidderId(bid.getBidder().getId())
                .build();
    }

    public static List<BidDTO> fromBids(List<Bid> bids) {
        return bids
                .stream()
                .map(BidDTO::fromBid)
                .collect(Collectors.toList());
    }
}

