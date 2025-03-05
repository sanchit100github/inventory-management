package com.example.inventory_management.model;

import lombok.Data;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "payments")
public class Payment {
    @Id
    private String paymentId;           // Unique identifier for each payment
    private String orderId;             // Reference to the associated order
    private double amount;              // Amount paid
    private String orderType;
    private String paymentMethod;       // Payment method (e.g., "Credit Card", "PayPal")
    private Role addedby;
    private String paymentType;       // Payment status (e.g., "Pending", "Completed", "Failed")
    private LocalDateTime paymentDate;         // Date and time of the payment
}
