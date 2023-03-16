package com.ecom.craftbid.entity.listing;

import com.ecom.craftbid.entity.appuser.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

//todo add tags
//    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
//    private List<Tag> tag;
}
