package com.ecom.craftbid.dtos;


import com.ecom.craftbid.entities.listing.Listing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListingDTO {
    private long id;
    private String title;
    private boolean ended;
    private Date expirationDate;
    private Date creationDate;
    private String description;
    private Collection<String> photos;
    private Collection<BidDTO> bids;
    private Collection<TagDTO> tags;
    private long advertiserId;
    private long winnerId;


    public static ListingDTO fromListing(Listing listing) {
        return ListingDTO.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .ended(listing.isEnded())
                .expirationDate(listing.getExpirationDate())
                .creationDate(listing.getCreationDate())
                .description(listing.getDescription())
                .photos(new ArrayList<>(listing.getPhotos()))
                .bids(BidDTO.fromBids(new ArrayList<>(listing.getBids())))
                .advertiserId(listing.getAdvertiser() == null ? 0 : listing.getAdvertiser().getId())
                .winnerId(listing.getWinner() == null ? 0 : listing.getWinner().getId())
                .tags(TagDTO.fromTags(new ArrayList<>(listing.getTags())))
                .build();
    }

}
