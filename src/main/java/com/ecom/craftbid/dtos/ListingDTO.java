package com.ecom.craftbid.dtos;


import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListingDTO {
    private long id;
    private String title;
    private Boolean ended;
    private Date expirationDate;
    private Date creationDate;
    private String description;
    private Collection<String> photos;
    private Collection<BidDTO> bids;
    private Collection<TagDTO> tags;
    private long advertiserId;
    private long winnerId;
    private double avgBid;

    public static ListingDTO fromListing(Listing listing) {
        List<Bid> bids = listing.getBids() != null ? new ArrayList<>(listing.getBids()) : new ArrayList<>();
        double avgBid = calculateAvgBid(bids);

        return ListingDTO.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .ended(listing.getEnded())
                .expirationDate(listing.getExpirationDate())
                .creationDate(listing.getCreationDate())
                .description(listing.getDescription())
                .photos(listing.getPhotos() != null ? new ArrayList<>(listing.getPhotos()) : new ArrayList<>())
                .bids(BidDTO.fromBids(bids))
                .advertiserId(listing.getAdvertiser() == null ? 0 : listing.getAdvertiser().getId())
                .winnerId(listing.getWinner() == null ? 0 : listing.getWinner().getId())
                .tags(TagDTO.fromTags(listing.getTags() != null ? new ArrayList<>(listing.getTags()) : new ArrayList<>()))
                .avgBid(avgBid)
                .build();
    }

    private static double calculateAvgBid(List<Bid> bids) {
        if (bids == null || bids.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (Bid b : bids) {
            sum += b.getPrice();
        }
        return sum / bids.size();
    }

}
