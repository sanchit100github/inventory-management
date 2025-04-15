package com.example.inventory_management.repository;

import com.example.inventory_management.model.CustomerOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderRepository extends MongoRepository<CustomerOrder, String> {
    List<CustomerOrder> findByCustomerId(String customerId);
    List<CustomerOrder> findByStatus(String status);
    List<CustomerOrder> findAllByStatus(String status);
}

