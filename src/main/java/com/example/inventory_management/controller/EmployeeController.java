package com.example.inventory_management.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory_management.model.AuditLog;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.Role;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;
import com.example.inventory_management.service.AuditLogService;
import com.example.inventory_management.service.ProductService;
import com.example.inventory_management.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController("/employee")
public class EmployeeController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    AuditLogService auditLogService;

    @GetMapping("/getproductcategory") 
    public ResponseEntity<?> getPoductCategory() {
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        if(user.isPresent()) {
            for(Role it : user.get().getRoles()) {
                if(it.getName().startsWith("EMPLOYEE")) {
                    hasRole = true;
                    break;
                }
            }
            if(hasRole) {
                List<String> categories = userService.getCategories(user.get());
                return new ResponseEntity<>(categories,HttpStatus.OK);
            }
            return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }
    
    @PostMapping("/addnewproduct") 
    public ResponseEntity<?> addNewProduct(@RequestBody Product product) {
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        if(user.isPresent()) {
            for(Role it : user.get().getRoles()) {
                if(it.getName().startsWith("EMPLOYEE")) {
                    hasRole = true;
                    break;
                }
            }
            if(hasRole) {
                boolean test = productService.saveProduct(product);
                if(test) {
                    String access = productService.getManagerRoleByCategory(product.getMaincategory());
                    AuditLog log = new AuditLog(name, "ADD", "Added " + product.getName(), List.of(access));
                    auditLogService.saveAudit(log);
                    return new ResponseEntity<>("Product has been added", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Product was not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }
    
    @PutMapping("/updateproduct")
    public ResponseEntity<?> updateProduct(@RequestBody Product product) {
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);

        if (user.isPresent()) {
            for (Role it : user.get().getRoles()) {
                if (it.getName().startsWith("EMPLOYEE")) {
                    hasRole = true;
                    break;
                }
            }
            if (hasRole) {
                List<String> categories = userService.getCategories(user.get());
                if(categories.contains(product.getMaincategory())) {
                    Optional<Product> existingProduct = productService.getProductById(product.getProductId());
                    if (existingProduct.isPresent()) {
                        Product updatedProduct = productService.updateProduct(product.getProductId(), product);
                        if (updatedProduct!=null) {
                            String access = productService.getManagerRoleByCategory(product.getMaincategory());
                            AuditLog log = new AuditLog(name, "UPDATE", "Updated " + updatedProduct.getName(), List.of(access));
                            auditLogService.saveAudit(log);
                            return new ResponseEntity<>("Product has been updated", HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>("Product update failed", HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
                    }
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getproduct")
    public ResponseEntity<?> getProduct(@RequestBody Map<String, String> requestBody) {
        String id = requestBody.get("id");
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);

        if (user.isPresent()) {
            for (Role it : user.get().getRoles()) {
                if (it.getName().startsWith("EMPLOYEE")) {
                    hasRole = true;
                    break;
                }
            }
            if (hasRole) {
                List<String> categories = userService.getCategories(user.get());
                Optional<Product> existingProduct = productService.getProductById(id);
                if(existingProduct.isPresent() && categories.contains(existingProduct.get().getMaincategory())) {
                    return new ResponseEntity<>(existingProduct,HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>("Product not found or you do not have access to this product ", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/deleteproduct")
    public ResponseEntity<?> deteteProduct(@RequestBody Map<String, String> requestBody) {
        String id = requestBody.get("id");
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        if (user.isPresent()) {
            for (Role it : user.get().getRoles()) {
                if (it.getName().startsWith("EMPLOYEE")) {
                    hasRole = true;
                    break;
                }
            }
            if (hasRole) {
                List<String> categories = userService.getCategories(user.get());
                Optional<Product> existingProduct = productService.getProductById(id);
                Product product = existingProduct.get();
                if(existingProduct.isPresent() && categories.contains(existingProduct.get().getMaincategory())) {
                    productService.deleteProduct(id);
                    String access = productService.getManagerRoleByCategory(product.getMaincategory());
                    AuditLog log = new AuditLog(name, "DELETE", "deleted " + product.getName(), List.of(access));
                    auditLogService.saveAudit(log);
                }
                else {
                    return new ResponseEntity<>("Product not found or you do not have access to this product ", HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);  
    }

}
