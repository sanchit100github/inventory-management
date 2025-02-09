// package com.example.inventory_management.controller;

// import com.example.inventory_management.model.Product;
// import com.example.inventory_management.service.ProductService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/products")
// public class ProductController {

//     @Autowired
//     private ProductService productService;

//     // Create a new product
//     @PostMapping
//     public Product createProduct(@RequestBody Product product) {
//         return productService.saveProduct(product);
//     }

//     // Get all products
//     @GetMapping
//     public List<Product> getAllProducts() {
//         return productService.getAllProducts();
//     }

//     // Get filtered products
//     @GetMapping("/filter")
//     public List<Product> getFilteredProducts(
//             @RequestParam(required = false) String category,
//             @RequestParam(required = false) Double minPrice,
//             @RequestParam(required = false) Double maxPrice,
//             @RequestParam(required = false) Boolean inStock,
//             @RequestParam(required = false) String supplierId,
//             @RequestParam(required = false) String location,
//             @RequestParam(required = false) String search,
//             @RequestParam(required = false, defaultValue = "name") String sortBy,
//             @RequestParam(required = false, defaultValue = "asc") String order) {
//         return productService.getFilteredProducts(category, minPrice, maxPrice, inStock, supplierId, location, search, sortBy, order);
//     }

//     // Get product by ID
//     @GetMapping("/{id}")
//     public Optional<Product> getProductById(@PathVariable String id) {
//         return productService.getProductById(id);
//     }

//     // Update product
//     @PutMapping("/{id}")
//     public Product updateProduct(@PathVariable String id, @RequestBody Product product) {
//         return productService.updateProduct(id, product);
//     }

//     // Delete product
//     @DeleteMapping("/{id}")
//     public void deleteProduct(@PathVariable String id) {
//         productService.deleteProduct(id);
//     }
// }
