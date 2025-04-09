package com.example.inventory_management.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "temp_batches")
public class TempBatch {
    @Id
    private String tempId;
    private String batchId;      
    private String productId;
    private String OrderId;
    private double price;
    private double cost;
    private int quantity;
    private String supplierId;
    private LocalDateTime created;
}
