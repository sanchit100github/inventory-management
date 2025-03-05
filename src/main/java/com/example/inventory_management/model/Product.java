package com.example.inventory_management.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class Product {

    @Id
    private String productId;  // Unique identifier for the product
    @Indexed(unique = true)
    private String name;  // Actual name of the product (e.g., "Laptop XYZ")
    private String mainCategory;  // Category name (e.g., "Electronics", "Furniture")
    private String subCategory;  
    private Boolean active;
    private LocalDateTime timestamp;
    private String description;  // Product description
    private int stockLevel;  // Stock level of the product
    private int reorderLevel;  // Minimum stock level to trigger reordering
    @DBRef
    private List<Batch> batches;
    private Role addedby;
}
