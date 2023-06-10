package com.ecom.craftbid.entities.listing;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "listing_tags",
            joinColumns = {@JoinColumn(name = "listing_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    @Nonnull
    private List<Listing> listings = new ArrayList<>();

    public void addListing(Listing listing) {
        if (this.listings == null)
            this.listings = new ArrayList<>();

        this.listings.add(listing);

        if (!listing.getTags().contains(this))
            listing.getTags().add(this);
    }
    public List<Listing> getListings() {
        if(this.listings == null)
            this.listings = new ArrayList<>();
        return this.listings;
    }
}

