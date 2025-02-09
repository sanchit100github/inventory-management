package com.example.inventory_management.service;

import com.example.inventory_management.model.Order;
import com.example.inventory_management.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    // Save new order
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // Update order
    public Order updateOrder(String id, Order order) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            Order updatedOrder = existingOrder.get();

            // Update fields of the existing order
            updatedOrder.setCustomerId(order.getCustomerId());
            updatedOrder.setProducts(order.getProducts());
            updatedOrder.setTotalAmount(order.getTotalAmount());
            updatedOrder.setOrderDate(order.getOrderDate());
            updatedOrder.setStatus(order.getStatus());

            // Save and return updated order
            return orderRepository.save(updatedOrder);
        }
        return null; // Or throw an exception if the order is not found
    }

    // Delete order by ID
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }
}
