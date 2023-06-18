package com.ecom.craftbid.controllers;

import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.services.ListingService;
import com.ecom.craftbid.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserService userService;
    private final ListingService listingService;

    public AdminController(UserService userService, ListingService listingService) {
        this.userService = userService;
        this.listingService = listingService;
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsersAdmin(pageable);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listings")
    public ResponseEntity<Page<ListingDTO>> getAllListings(Pageable pageable) {
        Page<ListingDTO> listingDtos = listingService.findAllAdmin(pageable);
        return ResponseEntity.ok(listingDtos);
    }

    @DeleteMapping("/listings/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable long id) {
        listingService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }
}
