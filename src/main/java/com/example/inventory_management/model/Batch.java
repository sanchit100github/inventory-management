package com.example.inventory_management.model;

import lombok.Data;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "batches")
public class Batch {
    @Id
    private String batchId;      // Unique identifier for each customer
    private String productId;
    private double price;
    private double cost;
    private int quantity;
    private String supplierId;
    private LocalDateTime created;
}