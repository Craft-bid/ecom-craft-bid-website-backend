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

    private boolean ended;
    private Date expirationDate;
    private Date creationDate;

    private String description;

    @ElementCollection
    @Nonnull
    private List<String> photos = new ArrayList<>();

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    @Nonnull
    private List<Bid> bids;
    @ManyToOne
    private User advertiser;
    @ManyToOne
    private User winner;


    @ManyToMany(mappedBy="listings")
    @Nonnull
    private Set<Tag> tags = new HashSet<>();

    public Listing() {
        this.bids = new ArrayList<>();
        this.tags = new HashSet<>();
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
}