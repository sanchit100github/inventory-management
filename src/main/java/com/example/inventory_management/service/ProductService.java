package com.example.inventory_management.service;

import com.example.inventory_management.model.Product;
import com.example.inventory_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MongoTemplate mongoTemplate;  // Injecting MongoTemplate

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by ID
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    // Save new product
    public boolean saveProduct(Product product) {
        Product savedProduct = productRepository.save(product);  // Save the product
        return savedProduct != null;  // Check if the product is saved and return true/false
    }

    // Update product
    public Product updateProduct(String id, Product product) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product updatedProduct = existingProduct.get();
            updatedProduct.setName(product.getName());
            updatedProduct.setMaincategory(product.getMaincategory());
            updatedProduct.setSubcategory(product.getSubcategory());
            updatedProduct.setDescription(product.getDescription());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setCost(product.getCost());
            updatedProduct.setStockLevel(product.getStockLevel());
            updatedProduct.setReorderLevel(product.getReorderLevel());
            updatedProduct.setSupplier(product.getSupplier());
            return productRepository.save(updatedProduct);
        }
        return null; // Or throw an exception if the product is not found
    }

    // Delete product by ID
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public String getManagerRoleByCategory(String category) {
        Map<String, String> categoryToManager = new HashMap<>();
        
        // Mapping categories to their respective manager roles
        categoryToManager.put("Smartphones", "MANAGER_Electronics");
        categoryToManager.put("Laptops", "MANAGER_Electronics");
        categoryToManager.put("TV", "MANAGER_Electronics");
        categoryToManager.put("Refrigerators", "MANAGER_Electronics");
        categoryToManager.put("AC", "MANAGER_Electronics");
        
        categoryToManager.put("Sofa", "MANAGER_Furniture");
        categoryToManager.put("DiningTables", "MANAGER_Furniture");
        categoryToManager.put("StudyTables", "MANAGER_Furniture");
        categoryToManager.put("Chairs", "MANAGER_Furniture");
        categoryToManager.put("Bed", "MANAGER_Furniture");

        categoryToManager.put("Cricket", "MANAGER_Sports");
        categoryToManager.put("Football", "MANAGER_Sports");
        categoryToManager.put("Hockey", "MANAGER_Sports");
        categoryToManager.put("Volleyball", "MANAGER_Sports");
        categoryToManager.put("TableTennis", "MANAGER_Sports");

        categoryToManager.put("Plumbing", "MANAGER_Hardware");
        categoryToManager.put("Paint", "MANAGER_Hardware");
        categoryToManager.put("Electrical", "MANAGER_Hardware");
        categoryToManager.put("Gardening", "MANAGER_Hardware");
        categoryToManager.put("Power", "MANAGER_Hardware");

        categoryToManager.put("ActionFigures", "MANAGER_Toys");
        categoryToManager.put("Board", "MANAGER_Toys");
        categoryToManager.put("Educational", "MANAGER_Toys");
        categoryToManager.put("VideoGames", "MANAGER_Toys");
        categoryToManager.put("Stuffed", "MANAGER_Toys");

        // Get the manager role based on the category
        return categoryToManager.getOrDefault(category, "");
    }


    // Get filtered products
    public List<Product> getFilteredProducts(String category, Double minPrice, Double maxPrice, Boolean inStock, 
                                              String supplierId, String location, String search, String sortBy, String order) {
        Query query = new Query();

        // Apply category filter
        if (category != null) {
            query.addCriteria(Criteria.where("category").is(category));
        }

        // Apply price range filter
        if (minPrice != null && maxPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice).lte(maxPrice));
        }

        // Apply stock level filter (only in-stock products)
        if (inStock != null && inStock) {
            query.addCriteria(Criteria.where("stockLevel").gt(0));
        }

        // Apply supplier filter
        if (supplierId != null) {
            query.addCriteria(Criteria.where("supplier.supplierId").is(supplierId));
        }

        // Apply location filter
        if (location != null) {
            query.addCriteria(Criteria.where("location").is(location));
        }

        // Apply search filter (search in name and description)
        if (search != null) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("name").regex(search, "i"),
                    Criteria.where("description").regex(search, "i")
            ));
        }

        // Sorting logic
        if (sortBy != null && order != null) {
            Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            query.with(Sort.by(direction, sortBy));
        }

        return mongoTemplate.find(query, Product.class);  // Execute the query using MongoTemplate
    }
}
