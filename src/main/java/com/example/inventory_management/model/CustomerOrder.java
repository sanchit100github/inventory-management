package com.example.inventory_management.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "orders")
public class CustomerOrder {
    @Id
    private String orderId;     // Unique identifier for each order
    private String customerId;  // Reference to the customer who placed the order
    private List<OrderProduct> products;  // List of products ordered
    private double totalAmount;  // Total price of all products in the order
    private double profitOnProducts;
    private Role orderedby;
    private LocalDateTime timestamp;
    private String status;       // Status of the order (e.g., "Pending", "Shipped")
}
