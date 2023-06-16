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
    private List<String> photos;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    @Nonnull
    private List<Bid> bids;
    @ManyToOne
    private User advertiser;
    @ManyToOne
    private User winner;


    @ManyToMany(mappedBy="listings")
    @Nonnull
    private Set<Tag> tags;

    public Listing() {
    }

    public void addTag(Tag tag) {
        if(this.tags == null)
            this.tags = new HashSet<>();
        this.tags.add(tag);
        tag.addListing(this);
    }
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getListings().remove(this);
    }

    public void addBid(Bid bid) {
        if(this.bids == null)
            this.bids = new ArrayList<>();
        this.bids.add(bid);
        bid.setListing(this);
    }

    public void removeBid(Bid bid) {
        this.bids.remove(bid);
        bid.setListing(null);
    }
}