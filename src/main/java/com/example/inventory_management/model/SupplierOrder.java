package com.example.inventory_management.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "supplier_orders")
public class SupplierOrder {
    @Id
    private String orderId;     // Unique identifier for each order
    private String supplierId;  // Reference to the customer who placed the order
    private List<OrderProduct> products;  // List of products ordered
    private double totalAmount;  // Total price of all products in the order
    private Role orderedby;
    private LocalDateTime timestamp;
    private String status;       // Status of the order (e.g., "Pending", "Shipped")
}
