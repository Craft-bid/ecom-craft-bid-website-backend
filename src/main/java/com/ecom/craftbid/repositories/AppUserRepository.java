package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.appuser.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserModelByEmail(String email);
}
