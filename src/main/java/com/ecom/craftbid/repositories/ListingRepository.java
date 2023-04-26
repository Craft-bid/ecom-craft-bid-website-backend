package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByEndedFalseOrderByExpirationDateAsc();
    List<Listing> findByEndedFalseOrderByExpirationDateDesc();
    List<Listing> findByEndedTrueOrderByExpirationDateAsc();
    List<Listing> findByEndedTrueOrderByExpirationDateDesc();

    List<Listing> findByEndedFalse();
    List<Listing> findByEndedTrue();

    List<Listing> findByAdvertiserId(long id);
    List<Listing> findByWinnerId(long id);
    List<Listing> findByBidsId(long id);
    List<Listing> findByTitleContaining(String title);

}
