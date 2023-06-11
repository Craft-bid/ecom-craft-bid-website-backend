package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAll();

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    void deleteById(Long id);
}