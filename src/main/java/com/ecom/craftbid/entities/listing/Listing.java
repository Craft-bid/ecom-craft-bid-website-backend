package com.ecom.craftbid.entities.listing;

import com.ecom.craftbid.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 255)
    private String title;

    private Boolean ended;
    private Date expirationDate;
    private Date creationDate;

    @Column(length = 5000)
    private String description;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User advertiser;
    @ManyToOne
    private User winner;

    @ElementCollection
    private final List<String> photos = new ArrayList<>();

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Bid> bids = new ArrayList<>();

    @ManyToMany(mappedBy = "listings", cascade = CascadeType.ALL)
    private final Set<Tag> tags = new HashSet<>();

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