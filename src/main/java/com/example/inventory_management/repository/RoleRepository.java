package com.example.inventory_management.repository;

import com.example.inventory_management.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    
    // Custom query to find role by name
    Optional<Role> findByName(String name);
}
