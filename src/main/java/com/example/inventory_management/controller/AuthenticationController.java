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
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String name = authentication.getName();
            Optional<User> user = userRepository.findByEmailAndActive(name, true);

            if (user.isPresent()) {
                String roleName = user.get().getAssigned().getName();
                String userEmail = user.get().getEmail();
                Map<String, String> response = new HashMap<>();
                response.put("email", userEmail);

                if (roleName.equals("ADMIN")) {
                    response.put("role", "ADMIN");
                    return ResponseEntity.ok(response);
                } else if (roleName.startsWith("MANAGER")) {
                    response.put("role", "MANAGER");
                    return ResponseEntity.ok(response);
                } else if (roleName.startsWith("EMPLOYEE")) {
                    response.put("role", "EMPLOYEE");
                    return ResponseEntity.ok(response);
                }
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "User has no valid role"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }
}
