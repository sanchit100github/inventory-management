package com.example.inventory_management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.inventory_management.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    
    void deleteByEmailAndActive(String username, boolean active);
    Optional<User> findByEmailAndActive(String email, boolean active);
    List<User> findAllByActive(boolean active);
}