package com.example.inventory_management.model;

import lombok.Data;

@Data
public class OrderProduct {
    private String productId;  // Reference to the Product being ordered
    private int quantity;      // Quantity of this product in the order
    private double priceAtOrder;  // Price of the product at the time of the order
}
