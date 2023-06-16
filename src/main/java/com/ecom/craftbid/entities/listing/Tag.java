package com.ecom.craftbid.entities.listing;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@Builder
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
    @Setter(value = AccessLevel.NONE)
    @Singular
    private final List<Listing> listings = new ArrayList<>();

    public Tag() {
    }

    public void addListing(Listing listing) {
        if (!this.listings.contains(listing))
            this.listings.add(listing);
        if (!listing.getTags().contains(this))
            listing.addTag(this);
    }

    public List<Listing> getListings() {
        return this.listings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Tag tag))
            return false;
        return Objects.equals(getName(), tag.getName());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

