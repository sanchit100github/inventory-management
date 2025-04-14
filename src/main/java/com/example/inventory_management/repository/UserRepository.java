package com.example.inventory_management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.inventory_management.model.Role;
import com.example.inventory_management.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    void deleteByEmailAndActive(String username, boolean active);
    Optional<User> findByEmailAndActive(String email, boolean active);
    List<User> findAllByActive(boolean active);
    List<User> findAllByAssigned(Role assigned);
    Optional<User> findByEmail(String email);
}