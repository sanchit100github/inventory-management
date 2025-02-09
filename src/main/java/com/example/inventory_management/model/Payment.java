package com.example.inventory_management.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "payments")
public class Payment {
    @Id
    private String paymentId;           // Unique identifier for each payment
    private Order order;             // Reference to the associated order
    private double amount;              // Amount paid
    private String paymentMethod;       // Payment method (e.g., "Credit Card", "PayPal")
    private String paymentStatus;       // Payment status (e.g., "Pending", "Completed", "Failed")
    private String transactionId;       // Unique transaction identifier
    private String paymentDate;         // Date and time of the payment
}
