package com.ecom.craftbid.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/_ah/**")
public class GAEController
{
	@GetMapping
	public ResponseEntity<String> helloWorld() {
		return ResponseEntity.ok("Received _ah request!");
	}

}
