package com.ecom.craftbid.repository;

import com.ecom.craftbid.entity.appuser.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<User, Long> {
}
