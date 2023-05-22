package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.listing.Tag;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
