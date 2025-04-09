package com.example.inventory_management.model;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class OrderProduct {

    @Id
    private String orderId;
    private String productId;  // Reference to the Product being ordered
    private int quantity;      // Quantity of this product in the order
    private double priceAtOrder;  // Price of the product at the time of the order
    private double costAtOrder;
}
