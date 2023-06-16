package com.ecom.craftbid.entities.listing;

import com.ecom.craftbid.entities.user.User;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private Boolean ended;
    private Date expirationDate;
    private Date creationDate;

    private String description;

    @ElementCollection
    @Nonnull
    @Setter(value = AccessLevel.NONE)
    private List<String> photos;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    @Nonnull
    @Setter(value = AccessLevel.NONE)
    private List<Bid> bids;

    @ManyToOne
    private User advertiser;
    @ManyToOne
    private User winner;


    @ManyToMany(mappedBy = "listings", cascade = CascadeType.ALL)
    @Nonnull
    @Setter(value = AccessLevel.NONE)
    private Set<Tag> tags;

    public Listing() {
        bids = new ArrayList<>();
        photos = new ArrayList<>();
        tags = new HashSet<>();
    }

    public void addTag(Tag tag) {

        this.tags.add(tag);
        if (!tag.getListings().contains(this))
            tag.addListing(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getListings().remove(this);
    }

    public void addBid(Bid bid) {
        this.bids.add(bid);
        bid.setListing(this);
    }

    public void removeBid(Bid bid) {
        this.bids.remove(bid);
        bid.setListing(null);
    }

    public void addPhoto(String url) {
        photos.add(url);
    }

    public void removePhoto(String url) {
        photos.remove(url);
    }
}