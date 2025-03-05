package com.example.inventory_management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;  // MongoDB's unique identifier for the document
    private String actionBy;
    private String action;  // Email used as a unique identifier
    private String description;
    private LocalDateTime timestamp;
    private List<Role> accessTo;

    public AuditLog(String actionBy, String action, String description, List<Role> accessTo) {
        this.actionBy = actionBy;
        this.action = action;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.accessTo = accessTo;
    }

}

