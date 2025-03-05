package com.example.inventory_management.repository;

import com.example.inventory_management.model.Payment;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByOrderId(String orderId);
    List<Payment> findByPaymentMethod(String paymentMethod);
    List<Payment> findByPaymentType(String paymentType);
}
