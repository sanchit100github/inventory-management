package com.example.inventory_management.repository;

import com.example.inventory_management.model.Supplier;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SupplierRepository extends MongoRepository<Supplier, String> {
    
    List<Supplier> findByAddressContainingIgnoreCase(String address);
    List<Supplier> findAllByActive();
}
