package com.example.inventory_management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventory_management.model.Batch;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.Refill;
import com.example.inventory_management.model.Role;
import com.example.inventory_management.repository.RefillRepository;
import java.util.Comparator;

@Service
public class RefillService {

    @Autowired
    RefillRepository refillRepository;

    @Autowired
    ProductService productService;

    public Refill saveRefill(Refill refill) {
        Optional<Product> tempProduct = productService.getProductById(refill.getProductId());
        if(tempProduct.isPresent()) {
            List<Batch> batches = tempProduct.get().getBatches();
            Optional<Batch> latestBatch = batches.stream()
                .max(Comparator.comparing(Batch::getCreated));
            if(latestBatch.isPresent()) {
                refill.setCost(latestBatch.get().getCost());
                refill.setPrice(latestBatch.get().getPrice());
            }
            else {
                refill.setCost(0);
                refill.setPrice(0);
            }
        }
        else {
            return null;
        }
        refill.setTimestamp(LocalDateTime.now());
        return refillRepository.save(refill);  // Save the product
    }

    public Refill updateRefill(Refill refill) {
        return refillRepository.save(refill);
    } 

    public List<Refill> getRefillByStatus(String status, Role role) {
        return refillRepository.findAllByStatusAndSentTo(status, role);
    }
}
