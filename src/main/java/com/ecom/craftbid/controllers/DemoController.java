package com.ecom.craftbid.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/hello-world")
public class DemoController {
    @GetMapping
    public ResponseEntity<String> helloWorld() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        return ResponseEntity.ok("Hello from secured World: " + currentUserName + "!");
    }
}
