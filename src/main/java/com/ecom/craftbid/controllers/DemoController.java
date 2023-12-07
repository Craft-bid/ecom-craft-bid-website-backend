package com.ecom.craftbid.controllers;

import com.ecom.craftbid.dtos.HelloResponse;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/private/hello-world")
@Log
public class DemoController {
    @GetMapping
    public ResponseEntity<HelloResponse> helloWorld() {
        log.info("Hello World");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        return ResponseEntity.ok(new HelloResponse("Hello from secured World: " + currentUserName + "!"));
    }

}

