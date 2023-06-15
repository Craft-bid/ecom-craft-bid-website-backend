package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.listing.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    void deleteById(long id);
    List<Bid> findByBidderId(long userId);
}
