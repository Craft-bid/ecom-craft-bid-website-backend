package com.ecom.craftbid.dtos;

import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.user.User;
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

    public static BidDTO fromBid(Bid bid) {
        return BidDTO.builder()
                .id(bid.getId())
                .price(bid.getPrice())
                .description(bid.getDescription())
                .creationDate(bid.getCreationDate())
                .daysToDeliver(bid.getDaysToDeliver())
                .bidderName(bid.getBidder().getName())
                .build();
    }

    public static List<BidDTO> fromBids(List<Bid> bids) {
//        return bids
//                .stream()
//                .map(BidDTO::fromBid)
//                .collect(Collectors.toList());
        // TODO: fix xd
        Bid bid = new Bid();
        bid.setId(1);
        bid.setPrice(100);
        bid.setDescription("test");
        bid.setCreationDate(new Date());
        bid.setDaysToDeliver(5);
        User bidder = new User();
        bidder.setName("test");
        bid.setBidder(bidder);
        return List.of(BidDTO.fromBid(bid));
    }
}

