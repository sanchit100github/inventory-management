package com.example.inventory_management.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.inventory_management.model.Refill;
import com.example.inventory_management.model.Role;

@Repository
public interface RefillRepository extends MongoRepository<Refill, String> {

    List<Refill> findAllByStatusAndSentTo(String status, Role sentTo);
    
}
