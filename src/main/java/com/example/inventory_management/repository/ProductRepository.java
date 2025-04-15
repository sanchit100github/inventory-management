package com.example.inventory_management.repository;

import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.Role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findByProductId(String productId);

    List<Product> findAllByMainCategory(String role);

    List<Product> findAllByMainCategoryAndActive(String role, boolean b);

    List<Product> findAllByMainCategoryAndActiveFalse(String role);

    List<Product> findAllByAddedby(Role role);

    List<Product> findAllByActive(boolean b);

}
