package com.example.inventory_management.repository;

import com.example.inventory_management.model.Batch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends MongoRepository<Batch, String> {

    // Custom query method to find a batch by productId, cost, price, and supplierId
    Optional<Batch> findByProductIdAndCostAndPriceAndSupplierId(
            String productId, double cost, double price, String supplierId);
    List<Batch> findByProductId(String productId);
}

