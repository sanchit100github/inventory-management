package com.example.inventory_management.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory_management.model.AuditLog;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.Refill;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;
import com.example.inventory_management.service.AuditLogService;
import com.example.inventory_management.service.ProductService;
import com.example.inventory_management.service.SupplierService;
import com.example.inventory_management.service.UserService;
import com.example.inventory_management.service.RefillService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    SupplierService supplierService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    RefillService refillService;

    @GetMapping("/getproductcategory") 
    public ResponseEntity<?> getPoductCategory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        if(user.isPresent()) {
            if(user.get().getAssigned().getName().startsWith("EMPLOYEE")) {
                List<String> categories = userService.getCategories(user.get());
                return new ResponseEntity<>(categories,HttpStatus.OK);
            }
            return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    private Optional<User> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        return user;
    }
    
    @PostMapping("/createnewproduct") 
    public ResponseEntity<?> createNewProduct(@RequestBody Product product) {
        Optional<User> user = getUser();
        if(user.isPresent()) {
            if(user.get().getAssigned().getName().startsWith("EMPLOYEE")) {
                product.setAddedby(user.get().getAssigned());
                Product test = productService.saveProduct(product, user.get());
                if(test != null) {
                    AuditLog log = new AuditLog(user.get().getEmail(), "ADD", "Added product " + product.getName(), List.of(user.get().getAssigned().getAddedby()));
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
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("EMPLOYEE")) {
                List<String> categories = userService.getCategories(user.get());
                if(categories.contains(product.getMainCategory())) {
                    Optional<Product> existingProduct = productService.getProductById(product.getProductId());
                    if (existingProduct.isPresent()) {
                        Product updatedProduct = productService.updateProduct(product.getProductId(), product);
                        if (updatedProduct!=null) {
                            AuditLog log = new AuditLog(user.get().getEmail(), "UPDATE", "Updated product " + updatedProduct.getName(), List.of(user.get().getAssigned().getAddedby()));
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

    @PostMapping("/getproduct")
    public ResponseEntity<?> getProduct(@RequestBody Map<String, String> requestBody) {
        String id = requestBody.get("id");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("EMPLOYEE")) {
                List<String> categories = userService.getCategories(user.get());
                Optional<Product> existingProduct = productService.getProductById(id);
                if(existingProduct.isPresent() && categories.contains(existingProduct.get().getMainCategory())) {
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
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("EMPLOYEE")) {
                List<String> categories = userService.getCategories(user.get());
                Optional<Product> existingProduct = productService.getProductById(id);
                Product product = existingProduct.get();
                if(existingProduct.isPresent() && categories.contains(existingProduct.get().getMainCategory())) {
                    productService.deleteProduct(id);
                    AuditLog log = new AuditLog(user.get().getEmail(), "DELETE", "deleted product " + product.getName(), List.of(user.get().getAssigned().getAddedby()));
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

    @PostMapping("/addrefillrequest")
    public ResponseEntity<?> addRefillRequest(@RequestBody Refill refill) {
        Optional<User> user = getUser();
        if(user.isPresent()) {
            if(user.get().getAssigned().getName().startsWith("EMPLOYEE")) {
                refill.setAddedBy(user.get().getAssigned());
                refill.setSentTo(user.get().getAssigned().getAddedby());
                Refill test = refillService.saveRefill(refill);
                if(test != null) {
                    AuditLog log = new AuditLog(user.get().getEmail(), "ADD", "Added refill request " + refill.getRefillId(), List.of(user.get().getAssigned().getAddedby()));
                    auditLogService.saveAudit(log);
                    return new ResponseEntity<>("Request has been added", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Request was not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getlowstockalert")
    public ResponseEntity<?> getLowStockAlert() {
        Optional<User> user = getUser();
        if(user.isPresent()) {
            if(user.get().getAssigned().getName().startsWith("EMPLOYEE")) {
                List<Product> products = productService.getLowStockAlert(user.get().getAssigned());
                return new ResponseEntity<>(products,HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

}
