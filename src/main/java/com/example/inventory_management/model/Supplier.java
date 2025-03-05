package com.example.inventory_management.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "suppliers")
public class Supplier {

    @Id
    private String supplierId;  // Unique identifier for each supplier
    private String name;  // Name of the supplier (e.g., "Stokes-Johnson")
    private String contactEmail;  // Contact email of the supplier
    private boolean active;
    private String contactPhone;  // Contact phone number of the supplier
    private String address;  // Physical address of the supplier (e.g., "123 Supplier St, City, Country")
    private List<String> productTypes;  // List of product types supplied by the supplier (e.g., "Electronics", "Furniture")
    private LocalDateTime added;

}
