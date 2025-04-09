package com.example.inventory_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventory_management.model.TempBatch;
import com.example.inventory_management.repository.TempBatchRepository;

@Service
public class TempBatchService {

    @Autowired
    private TempBatchRepository tempBatchRepository;

    public TempBatch saveBatch(TempBatch tempbatch) {
        return tempBatchRepository.save(tempbatch);
    }

    public List<TempBatch> getByProductId(String productId) {
        return tempBatchRepository.findAllByProductId(productId);
    }

    public List<TempBatch> findAllByOrderId(String orderId) {
        return tempBatchRepository.findAllByOrderId(orderId);
    }

    public void deleteBatchById(String batchId) {
        tempBatchRepository.deleteById(batchId);
    }
}
