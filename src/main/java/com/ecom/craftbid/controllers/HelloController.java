package com.ecom.craftbid.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
@RequiredArgsConstructor
public class HelloController {
    @GetMapping("/message")
    public String showMessage() {
        return "Hello from public endpoint";
    }

    @GetMapping("/protected")
    public String showProtectedMessage() {
        return "Hello from the other site";
    }
}
