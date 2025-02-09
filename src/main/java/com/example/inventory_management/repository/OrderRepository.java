package com.example.inventory_management.repository;

import com.example.inventory_management.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
    
}
