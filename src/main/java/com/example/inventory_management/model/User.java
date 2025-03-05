package com.example.inventory_management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;  // MongoDB's unique identifier for the document
    private String email;  // Email used as a unique identifier
    private String password;
    
    private boolean active;  // Account status
    private String contact;    // Contact info (e.g., phone number)
    @DBRef
    private Role assigned;
    private double salary;

}

