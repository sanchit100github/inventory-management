package com.example.inventory_management.model;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "customers")
public class Customer {
    @Id
    private String customerId;      // Unique identifier for each customer
    private String name;       // First name of the customer
    private String email;           // Email address of the customer
    private String contact;     // Phone number of the customer
    private String address;         // Shipping address of the customer
}
