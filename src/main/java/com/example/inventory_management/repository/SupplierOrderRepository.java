package com.example.inventory_management.repository;

import com.example.inventory_management.model.SupplierOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierOrderRepository extends MongoRepository<SupplierOrder, String> {
    List<SupplierOrder> findAllBySupplierId(String supplierId);
    List<SupplierOrder> findAllByStatus(String status);
}

