package com.example.inventory_management.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class Product {

    @Id
    private String productId;  // Unique identifier for the product
    private String name;  // Actual name of the product (e.g., "Laptop XYZ")
    private String maincategory;  // Category name (e.g., "Electronics", "Furniture")
    private String subcategory;  
    private String description;  // Product description
    private double price;  // Product price
    private double cost;  // Product cost
    private int stockLevel;  // Stock level of the product
    private int reorderLevel;  // Minimum stock level to trigger reordering
    private Supplier supplier;  // Reference to Supplier model
}
