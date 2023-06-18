package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.user.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
