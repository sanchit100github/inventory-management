package com.example.inventory_management.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmailAndActive(username, true);
        
        if (user.isPresent()) {
            System.out.println("User Found: " + user.get().getEmail());
            System.out.println("Stored Password: " + user.get().getPassword());
            System.out.println("Roles: " + user.get().getAssigned());
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.get().getEmail())
                    .password(user.get().getPassword()) // Password must be BCrypt encoded
                    // .roles(user.get().getRoles().toArray(new String[0])) // Roles must match "ROLE_ADMIN"
                    .build();
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}