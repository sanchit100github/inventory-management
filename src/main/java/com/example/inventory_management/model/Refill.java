package com.example.inventory_management.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "refill")
public class Refill {

    @Id
    private String refillId;
    private String productId;
    private int quantity;
    private Role addedBy;
    private Role sentTo;
    private String priority;
    private String additional;
    private double cost;
    private double price;
    private LocalDateTime timestamp;
    private String status;

}
