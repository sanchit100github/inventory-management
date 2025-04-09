package com.example.inventory_management.service;

import java.util.List;
import java.util.Map;
import com.example.inventory_management.model.Batch;
import com.example.inventory_management.model.OrderProduct;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.TempBatch;
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

    @Autowired
    private TempBatchService tempBatchService;

    @Async
    public void updateInventory(Map<String, Integer> mapBatches, Map<OrderProduct, Integer> mapProducts, String orderId) {
        for (Map.Entry<String, Integer> entry : mapBatches.entrySet()) {
            Optional<Batch> batchOptional = batchService.getBatchById(entry.getKey());
            if (batchOptional.isPresent()) {
                Batch batch = batchOptional.get();
                TempBatch tempBatch = new TempBatch();
                tempBatch.setBatchId(batch.getBatchId());
                tempBatch.setProductId(batch.getProductId());
                tempBatch.setPrice(batch.getPrice());
                tempBatch.setCost(batch.getCost());
                tempBatch.setQuantity(entry.getValue());
                tempBatch.setSupplierId(batch.getSupplierId());
                tempBatch.setCreated(batch.getCreated());
                tempBatch.setOrderId(orderId);
                tempBatchService.saveBatch(tempBatch);

                if (entry.getValue().equals(batch.getQuantity())) {
                    Optional<Product> product = productService.getProductById(batch.getProductId());
                    product.get().getBatches().remove(batch);
                    batchService.deleteBatchById(entry.getKey());  // Remove batch if fully used
                } else {
                    batch.setQuantity(batch.getQuantity() - entry.getValue());
                    batchService.saveBatch(batch);  // Update batch quantity
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
