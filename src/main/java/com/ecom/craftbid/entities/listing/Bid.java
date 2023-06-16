package com.ecom.craftbid.entities.listing;

import com.ecom.craftbid.entities.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @ManyToOne()
//    @JoinColumn(name = "listing_id")
    private Listing listing;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Bid )) return false;
//        return id != 0 && id == (((Bid) o).getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }
}