package com.ecom.craftbid.dtos;


import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.listing.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

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
    private List<String> photos = new ArrayList<>();
    private List<BidDTO> bids;
    private long advertiserId;
    private long winnerId;
    private Collection<TagDTO> tags;

    public static ListingDTO fromListing(Listing listing) {
        Set<Tag> tags2 = listing.getTags();
        List<TagDTO> tags1 = new ArrayList<>(); // TODO: fix xd
        return ListingDTO.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .ended(listing.isEnded())
                .expirationDate(listing.getExpirationDate())
                .creationDate(listing.getCreationDate())
                .description(listing.getDescription())
                .photos(listing.getPhotos())
                .bids(BidDTO.fromBids(listing.getBids()))
                .advertiserId(listing.getAdvertiser().getId())
                .winnerId(listing.getWinner() == null ? 0 : listing.getWinner().getId())
                .tags(tags1)
                .build();
    }

}
