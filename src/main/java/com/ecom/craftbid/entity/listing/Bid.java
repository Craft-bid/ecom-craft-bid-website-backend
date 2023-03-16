package com.ecom.craftbid.entity.listing;

import com.ecom.craftbid.entity.appuser.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long price;
    private String description;
    private Date creationDate;
    private long daysToDeliver;

    @OneToOne
    private User bidder;

    @ManyToOne
    private Listing listing;
}
