package com.example.inventory_management.service;

import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.Role;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MongoTemplate mongoTemplate;  // Injecting MongoTemplate

    // Get all products
    public List<Product> getAllProducts(User user) {
        return productRepository.findAllByMainCategoryAndActive(user.getAssigned().getName().replace("EMPLOYEE_", ""), true);
    }

    // Get product by ID
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    // Save new product
    public Product saveProduct(Product product, User user) {
        product.setMainCategory(user.getAssigned().getName().replaceFirst("EMPLOYEE_", ""));
        product.setActive(true);
        product.setTimestamp(LocalDateTime.now());
        return productRepository.save(product);  // Save the product
    }

    public Product save(Product product) {
        return productRepository.save(product);  // Save the product
    }

    // Update product
    public Product updateProduct(String id, Product product) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            existingProduct.get().setName(product.getName());
            existingProduct.get().setSubCategory(product.getSubCategory());
            existingProduct.get().setDescription(product.getDescription());
            existingProduct.get().setStockLevel(product.getStockLevel());
            existingProduct.get().setReorderLevel(product.getReorderLevel());
            if(existingProduct.get().getActive().equals(false) && product.getActive().equals(true)) {
                existingProduct.get().setTimestamp(LocalDateTime.now());
            }
            return productRepository.save(existingProduct.get());
        }
        return null; // Or throw an exception if the product is not found
    }

    // Delete product by ID
    public Product deleteProduct(String id) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if(existingProduct.isPresent()) {
            existingProduct.get().setActive(false);
            return productRepository.save(existingProduct.get());
        }
        return null;
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

	public List<Product> getByActiveAndMonthAndYearAndRole(String role, Integer month, Integer year) {
		List<Product> products = productRepository.findAllByMainCategoryAndActive(role, true);
        List<Product> finalList = new ArrayList<>();
        for(Product it : products) {
            if(it.getTimestamp().getMonthValue() == month && it.getTimestamp().getYear() == year) {
                finalList.add(it);
            }
        }
        return finalList;
	}

    public List<Product> getByNotActiveAndMonthAndYearAndRole(String role, Integer month, Integer year) {
        List<Product> products = productRepository.findAllByMainCategoryAndActiveFalse(role);
        List<Product> finalList = new ArrayList<>();
        for(Product it : products) {
            if(it.getTimestamp().getMonthValue() == month && it.getTimestamp().getYear() == year) {
                finalList.add(it);
            }
        }
        return finalList;
    }

    public List<Product> getLowStockAlert(Role role) {
        List<Product> products = productRepository.findAllByAddedby(role);
        List<Product> finalList = new ArrayList<>();
        for(Product it : products) {
            if(it.getReorderLevel()>=it.getStockLevel()) {
                finalList.add(it);
            }
        }
        return finalList;
    }
}
