package com.example.inventory_management.repository;

import com.example.inventory_management.model.Payment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByOrderId(String orderId);
    List<Payment> findByStatus(String paymentType);
    Optional<Payment> findById(String id);
}
