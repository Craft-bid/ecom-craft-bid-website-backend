package com.ecom.craftbid.entities.transaction;


import com.ecom.craftbid.entities.listing.Bid;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.user.PersonalData;
import com.ecom.craftbid.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long price;
    private Date creationDate;
    private Date acceptanceDate;
    private long daysToDeliver;

    @OneToOne
    private Bid bid;
    @OneToOne
    private Listing listing;
    @OneToOne
    private User bidder; // Who have won the bid
    @OneToOne
    private User creator; // Of a listing

    /* Add short for address, BAdd short for BillingAddress */
    @OneToOne
    @JoinColumn(name = "delivery_add")
    private PersonalData deliveryAddress; // Ones who created the listing
    @OneToOne
    @JoinColumn(name = "origin_add")
    private PersonalData originAddress; // Ones who fulfill the commission from the listing
    @OneToOne
    @JoinColumn(name = "delivery_b_add")
    private PersonalData deliveryBillingAddress;
    @OneToOne
    @JoinColumn(name = "origin_b_add")
    private PersonalData originBillingAddress;

    /* Will most importantly include payment info, optionally special requests etc. */
    private String description;
}
