package com.example.inventory_management.repository;

import com.example.inventory_management.model.TempBatch;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempBatchRepository extends MongoRepository<TempBatch, String>{

    List<TempBatch> findAllByProductId(String productId);
    List<TempBatch> findAllByOrderId(String orderId);
    
}
