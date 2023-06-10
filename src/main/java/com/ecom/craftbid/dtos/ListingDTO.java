package com.ecom.craftbid.dtos;


import com.ecom.craftbid.entities.listing.Listing;
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
    private UserDTO advertiser;
    private UserDTO winner;
    private Collection<TagDTO> tags;

    public static ListingDTO fromListing(Listing listing) {
        return ListingDTO.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .ended(listing.isEnded())
                .expirationDate(listing.getExpirationDate())
                .creationDate(listing.getCreationDate())
                .description(listing.getDescription())
                .photos(listing.getPhotos())
                .bids(BidDTO.fromBids(listing.getBids()))
                .advertiser(UserDTO.fromUser(listing.getAdvertiser()))
                .winner(UserDTO.fromUser(listing.getWinner()))
                .tags(TagDTO.fromTags(listing.getTags()))
                .build();
    }

}
