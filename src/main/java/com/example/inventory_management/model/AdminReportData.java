package com.example.inventory_management.model;

import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminReportData {
    private List<String> supplierNames;
    private List<String> customerNames;
    private List<String> newCustomers;
    private List<String> newSuppliers;
    private Map<String, Pair<List<String>, Integer>> supplierOrdersMap;
    private Map<String, Pair<List<String>, Integer>> customerOrdersMap;
    private Map<String, Pair<List<String>, Pair<Integer, Double>>> supplierPaymentsMap;
    private Map<String, Pair<List<String>, Pair<Integer, Double>>> customerPaymentsMap;
    private Map<String, Double> godownAggregate;
    private double paymentsSupplier;
    private double paymentsCustomer;
    private double paymentsTotal;
    private double ordersSupplier;
    private double ordersCustomer;
    private double ordersTotal;

}

