package com.example.inventory_management.controller;

import com.example.inventory_management.model.Supplier;
import com.example.inventory_management.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // Create a new supplier
    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierService.saveSupplier(supplier);
    }

    // Get all suppliers
    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    // Get supplier by ID
    @GetMapping("/{id}")
    public Optional<Supplier> getSupplierById(@PathVariable String id) {
        return supplierService.getSupplierById(id);
    }

    // Update supplier
    @PutMapping("/{id}")
    public Supplier updateSupplier(@PathVariable String id, @RequestBody Supplier supplier) {
        return supplierService.updateSupplier(id, supplier);
    }

    // Delete supplier
    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable String id) {
        supplierService.deleteSupplier(id);
    }

    @GetMapping("/search")
    public List<Supplier> getSuppliersByAddress(@RequestParam String address) {
        return supplierService.findSuppliersByAddress(address);
    }
}
