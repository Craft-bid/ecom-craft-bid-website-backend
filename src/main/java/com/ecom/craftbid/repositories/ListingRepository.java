package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.listing.Listing;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    Page<Listing> findByEndedFalseOrderByExpirationDateAsc(Pageable pageable);
    Page<Listing> findByEndedFalseOrderByExpirationDateDesc(Pageable pageable);
    Page<Listing> findByEndedTrueOrderByExpirationDateAsc(Pageable pageable);
    Page<Listing> findByEndedTrueOrderByExpirationDateDesc(Pageable pageable);

    Page<Listing> findByEndedFalse(Pageable pageable);
    Page<Listing> findByEndedTrue(Pageable pageable);

    Page<Listing> findByAdvertiserId(long id, Pageable pageable);
    Page<Listing> findByWinnerId(long id, Pageable pageable);
    Page<Listing> findByBidsId(long id, Pageable pageable);
    Page<Listing> findByTitleContaining(String title, Pageable pageable);

    Page<Listing> findByTags_Name(String name, Pageable pageable);

    Listing save(@NonNull Listing listing);
    void deleteById(long id);


}
