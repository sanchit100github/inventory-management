package com.example.inventory_management.service;

import java.util.List;
import java.util.Map;
import com.example.inventory_management.model.Batch;
import com.example.inventory_management.model.OrderProduct;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.User;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private BatchService batchService;

    @Autowired
    private ProductService productService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Async
    public void updateInventory(Map<String, Integer> mapBatches, Map<OrderProduct, Integer> mapProducts) {
        for (Map.Entry<String, Integer> entry : mapBatches.entrySet()) {
            Optional<Batch> batch = batchService.getBatchById(entry.getKey());
            if (batch.isPresent()) {
                if (entry.getValue().equals(batch.get().getQuantity())) {
                    Optional<Product> product = productService.getProductById(batch.get().getProductId());
                    product.get().getBatches().remove(batch.get());
                    batchService.deleteBatchById(entry.getKey());  // Remove batch if fully used
                } else {
                    batch.get().setQuantity(batch.get().getQuantity() - entry.getValue());
                    batchService.saveBatch(batch.get());  // Update batch quantity
                }
            }
        }
        for (Map.Entry<OrderProduct, Integer> entry : mapProducts.entrySet()) {
            Optional<Product> product1 = productService.getProductById(entry.getKey().getProductId());
            Product product = product1.get();
            List<User> users = userService.findByAssigned(product.getAddedby());
            product.setStockLevel(product.getStockLevel()-entry.getValue());
            productService.saveProduct(product);
            if(product.getStockLevel() <= product.getReorderLevel()) {
                for(User it : users) {
                    emailService.sendLowStockAlert(product.getName(), product.getStockLevel(), it.getEmail());
                }
            }
        }
    }
}
