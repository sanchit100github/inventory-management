package com.example.inventory_management.service;

import com.example.inventory_management.model.Payment;
import com.example.inventory_management.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SupplierOrderService supplierOrderService;

    @Autowired
    private CustomerOrderService customerOrderService;

    // Get all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Get payment by ID
    public Optional<Payment> getPaymentById(String id) {
        return paymentRepository.findById(id);
    }

    public Payment addPayment(Payment payment) {
        payment.setPaymentDate(LocalDateTime.now());
        if(payment.getOrderType().equals("Supplier") && !supplierOrderService.getOrderById(payment.getOrderId()).isPresent() ||
           payment.getOrderType().equals("Customer") && !customerOrderService.getOrderById(payment.getOrderId()).isPresent()) {
            return null;
        }
        return paymentRepository.save(payment);
    }

    // Save new payment
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Update payment
    public Payment updatePayment(Payment payment) {
        Optional<Payment> existingPayment = paymentRepository.findById(payment.getPaymentId());
        if (existingPayment.isPresent()) {
            Payment updatedPayment = existingPayment.get();

            // Update fields of the existing payment
            updatedPayment.setAmount(payment.getAmount());
            updatedPayment.setStatus(payment.getStatus());

            // Save and return updated payment
            return paymentRepository.save(updatedPayment);
        }
        return null; // Or throw an exception if payment is not found
    }

    public List<Payment> findAllByRole(String role) {
        List<Payment> payments = getAllPayments();
        List<Payment> finalList = new ArrayList<>();
        for(Payment it : payments) {
            if(it.getAddedby().getName().equals(role)) {
                finalList.add(it);
            }
        }
        return finalList;
    }

}
