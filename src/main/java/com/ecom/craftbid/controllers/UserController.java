package com.ecom.craftbid.controllers;

import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/public/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/private/users")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/private/users/{id}")
    public Optional<User> getUser(@PathVariable long id) {
        return userRepository.findById(id);
    }

    @DeleteMapping("/private/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/private/users/{id}/password")
    public User updateUserPassword(@PathVariable long id, @RequestBody String password) {
        User user = userRepository.findById(id).get();
        user.setPassword(password);
        return userRepository.save(user);
    }


}
