package com.ecom.craftbid.entity.listing;

import com.ecom.craftbid.entity.appuser.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
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
    private List<String> photos = new ArrayList<>();

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<Bid> bids;
    @ManyToOne
    private User advertiser;
    @ManyToOne
    private User winner;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Tag> tags = new HashSet<>();
}
