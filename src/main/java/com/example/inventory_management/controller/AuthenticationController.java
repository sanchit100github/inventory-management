package com.example.inventory_management.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.inventory_management.model.LoginRequest;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")

@RestController
public class AuthenticationController {

    @Autowired
    UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Create an authentication token
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String name = authentication.getName();
            Optional<User> user = userRepository.findByEmailAndActive(name, true);
            if(user.isPresent()) {
                if(user.get().getAssigned().getName().equals("ADMIN")) {
                    return new ResponseEntity<>("ADMIN", HttpStatus.OK);
                }
                else if(user.get().getAssigned().getName().startsWith("MANAGER")) {
                    return new ResponseEntity<>("MANAGER", HttpStatus.OK);
                }
                else if(user.get().getAssigned().getName().startsWith("EMPLOYEE")) {
                    return new ResponseEntity<>("EMPLOYEE", HttpStatus.OK);
                }  
            }
            return new ResponseEntity<>("User has no valid role", HttpStatus.FORBIDDEN);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }
}
