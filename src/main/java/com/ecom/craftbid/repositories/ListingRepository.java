package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.listing.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {

}
