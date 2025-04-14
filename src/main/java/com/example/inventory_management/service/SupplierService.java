package com.example.inventory_management.service;

import com.example.inventory_management.model.Supplier;
import com.example.inventory_management.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    // Get all suppliers
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAllByActive(true);
    }

    // Get supplier by ID
    public Optional<Supplier> getSupplierById(String id) {
        return supplierRepository.findById(id);
    }

    // Save new supplier
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    // Update supplier
    public Supplier updateSupplier(Supplier supplier) {
        Optional<Supplier> existingSupplier = supplierRepository.findById(supplier.getSupplierId());
        if (existingSupplier.isPresent()) {
            Supplier updatedSupplier = existingSupplier.get();
            
            // Update fields (this can be adjusted as needed)
            updatedSupplier.setName(supplier.getName());
            updatedSupplier.setContactEmail(supplier.getContactEmail());
            updatedSupplier.setAddress(supplier.getAddress());
            
            // Save and return updated supplier
            return supplierRepository.save(updatedSupplier);
        }
        return null; // Or throw an exception if supplier not found
    }

    // Delete supplier by ID
    public void deleteSupplier(String id) {
        Optional<Supplier> existingSupplier = supplierRepository.findById(id);
        if (existingSupplier.isPresent()) {
            Supplier updatedSupplier = existingSupplier.get();
            
            updatedSupplier.setActive(false);
            supplierRepository.save(updatedSupplier);
        }
        
    }

    public List<Supplier> findSuppliersByAddress(String address) {
        return supplierRepository.findByAddressContainingIgnoreCase(address);
    }

    public List<Supplier> findSuppliersByActive(boolean active) {
        return supplierRepository.findAllByActive(active);
    }

    public Optional<Supplier> getSupplierByName(String name) {
        return supplierRepository.findByName(name);
    }

    
}
