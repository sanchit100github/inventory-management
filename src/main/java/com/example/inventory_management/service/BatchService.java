package com.example.inventory_management.service;

import com.example.inventory_management.model.Batch;
import com.example.inventory_management.repository.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BatchService {

    @Autowired
    private BatchRepository batchRepository;

    // Get all batches
    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    // Get batch by ID
    public Optional<Batch> getBatchById(String batchId) {
        return batchRepository.findById(batchId);
    }

    // Save or update a batch
    public Batch saveBatch(Batch batch) {
        return batchRepository.save(batch);
    }

    public List<Batch> findByProductId(String productId) {
        return batchRepository.findByProductId(productId);
    }

    // Get batch by productId, cost, price, and supplierId
    public Optional<Batch> getBatchByCriteria(String productId, double cost, double price, String supplierId) {
        return batchRepository.findByProductIdAndCostAndPriceAndSupplierId(productId, cost, price, supplierId);
    }

    public Optional<Batch> updateBatch(String batchId, Batch updatedBatch) {
        return batchRepository.findById(batchId).map(existingBatch -> {
            existingBatch.setProductId(updatedBatch.getProductId());
            existingBatch.setCost(updatedBatch.getCost());
            existingBatch.setPrice(updatedBatch.getPrice());
            existingBatch.setQuantity(updatedBatch.getQuantity());
            existingBatch.setSupplierId(updatedBatch.getSupplierId());

            return batchRepository.save(existingBatch);
        });
    }

    public void deleteBatchById(String batchId) {
        batchRepository.deleteById(batchId);
    }
}

