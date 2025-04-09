package com.example.inventory_management.service;

import com.example.inventory_management.model.Batch;
import com.example.inventory_management.model.OrderProduct;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.SupplierOrder;
import com.example.inventory_management.repository.SupplierOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SupplierOrderService {

    @Autowired
    BatchService batchService;

    @Autowired
    ProductService productService;

    @Autowired
    private SupplierOrderRepository supplierOrderRepository;

    // Get all supplier orders
    public List<SupplierOrder> getAllOrders() {
        return supplierOrderRepository.findAll();
    }

    // Get supplier order by ID
    public Optional<SupplierOrder> getOrderById(String orderId) {
        return supplierOrderRepository.findById(orderId);
    }

    // Get orders by supplier ID
    public List<SupplierOrder> getOrdersBySupplier(String supplierId) {
        return supplierOrderRepository.findAllBySupplierId(supplierId);
    }

    // Get orders by status (e.g., "Pending", "Shipped")
    public List<SupplierOrder> getOrdersByStatus(String status) {
        return supplierOrderRepository.findAllByStatus(status);
    }
    // Create or update a supplier order
    public SupplierOrder saveNewOrder(SupplierOrder order) {
        double total=0;
        for(OrderProduct it : order.getProducts()) {
            if(!productService.getProductById(it.getProductId()).isPresent()) {
                return null;
            }
            total+=it.getCostAtOrder();
        }
        order.setTotalAmount(total);
        order.setStatus("Pending");
        order.setTimestamp(LocalDateTime.now());
        return supplierOrderRepository.save(order);
    }

    
    // Update order status
    public SupplierOrder updateOrderStatus(String orderId, String status) {
        Optional<SupplierOrder> existingOrder = supplierOrderRepository.findById(orderId);
        SupplierOrder order = existingOrder.get();
        order.setStatus(status);
        if(status.equals("Completed")) {
            for(OrderProduct it : order.getProducts()) {
                Optional<Batch> batch = batchService.getBatchByCriteria(it.getProductId(), it.getCostAtOrder(), it.getPriceAtOrder(), order.getSupplierId());
                if(batch.isPresent()) {
                    Batch batch1=batch.get();
                    batch1.setQuantity(batch.get().getQuantity()+it.getQuantity());
                    batchService.saveBatch(batch1);
                }
                else {
                    Batch newBatch = new Batch();
                    newBatch.setCost(it.getCostAtOrder());
                    newBatch.setPrice(it.getPriceAtOrder());
                    newBatch.setProductId(it.getProductId());
                    newBatch.setSupplierId(order.getSupplierId());
                    newBatch.setQuantity(it.getQuantity());
                    newBatch.setCreated(LocalDateTime.now());
                    batchService.saveBatch(newBatch);
                    Optional<Product> product = productService.getProductById(it.getProductId());
                    if(product.isPresent()) {
                        product.get().getBatches().add(newBatch);
                    }
                    else {
                        return null;
                    }
                }
            }
        }
        return supplierOrderRepository.save(order);
    }

    // Delete an order by ID
    public void deleteOrder(String orderId) {
        supplierOrderRepository.deleteById(orderId);
    }

    public List<SupplierOrder> findAllByMonthAndYearAndRole(String role, int month, int year) {
        List<SupplierOrder> supplierOrder = findAllByMonthAndYear(month, year);
        List<SupplierOrder> finalList = new ArrayList<>();
        for(SupplierOrder it : supplierOrder) {
            if(it.getOrderedby().getName().equals(role)) {
                finalList.add(it);
            }
        }
        return finalList;
    }

    public List<SupplierOrder> findAllByMonthAndYear(int month, int year) {
        List<SupplierOrder> supplierOrder = supplierOrderRepository.findAll();
        List<SupplierOrder> finalList = new ArrayList<>();
        for(SupplierOrder it : supplierOrder) {
            if(it.getTimestamp().getMonthValue() == month && it.getTimestamp().getYear() == year) {
                finalList.add(it);
            }
        }
        return finalList;
    }
}
