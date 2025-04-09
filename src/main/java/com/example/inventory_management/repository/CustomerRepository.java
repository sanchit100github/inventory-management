package com.example.inventory_management.repository;

import com.example.inventory_management.model.Customer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
        List<Customer> findAllByActive();
        Optional<Customer> findByName(String name);
}
