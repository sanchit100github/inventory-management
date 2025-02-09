package com.example.inventory_management.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory_management.model.Role;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@RestController
public class AccountController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String demo() {
        return "Hello World";
    }

    @GetMapping("/test1")
    public ResponseEntity<?> test1() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        if(user.isPresent()) {
            for(Role it : user.get().getRoles()) {
                if(it.getName().equals("ADMIN")) {
                    return new ResponseEntity<>("Access is approved", HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/test2")
    public String test2() {
        return "Test API2";
    }
}
