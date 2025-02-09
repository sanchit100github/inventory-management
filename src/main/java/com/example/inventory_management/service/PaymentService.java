package com.example.inventory_management.service;

import com.example.inventory_management.model.Payment;
import com.example.inventory_management.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Get all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Get payment by ID
    public Optional<Payment> getPaymentById(String id) {
        return paymentRepository.findById(id);
    }

    // Save new payment
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Update payment
    public Payment updatePayment(String id, Payment payment) {
        Optional<Payment> existingPayment = paymentRepository.findById(id);
        if (existingPayment.isPresent()) {
            Payment updatedPayment = existingPayment.get();

            // Update fields of the existing payment
            updatedPayment.setAmount(payment.getAmount());
            updatedPayment.setPaymentMethod(payment.getPaymentMethod());
            updatedPayment.setPaymentStatus(payment.getPaymentStatus());
            updatedPayment.setTransactionId(payment.getTransactionId());
            updatedPayment.setPaymentDate(payment.getPaymentDate());

            // Save and return updated payment
            return paymentRepository.save(updatedPayment);
        }
        return null; // Or throw an exception if payment is not found
    }

    // Delete payment by ID
    public void deletePayment(String id) {
        paymentRepository.deleteById(id);
    }
}
