package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.listing.Listing;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
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
    Listing findById(long id);

    Page<Listing> findByTags_NameIn(List<String> names, Pageable pageable);

    Page<Listing> findAll(Specification<Listing> spec, Pageable pageable);

    void deleteById(long id);


}
