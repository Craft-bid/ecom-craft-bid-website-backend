package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.listing.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
