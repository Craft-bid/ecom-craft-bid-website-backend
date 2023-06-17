package com.ecom.craftbid.controllers;

import com.ecom.craftbid.dtos.UserDTO;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/public/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/public/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/public/users/myId")
    public ResponseEntity<Long> getMyId(@RequestBody String jwtToken) {
        Long id = userService.getMyId(jwtToken);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/private/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @DeleteMapping("/private/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/private/users/{id}/password")
    public ResponseEntity<User> updateUserPassword(@PathVariable long id, @RequestBody String password) {
        User updatedUser = userService.updateUserPassword(id, password);
        return ResponseEntity.ok(updatedUser);
    }
}

