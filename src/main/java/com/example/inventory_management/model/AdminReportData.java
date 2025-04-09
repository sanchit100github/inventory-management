package com.example.inventory_management.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;

@Data
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

    public AdminReportData(List<String> supplierNames,
                           List<String> customerNames,
                           List<String> newCustomers,
                           List<String> newSuppliers,
                           Map<String, Pair<List<String>, Integer>> supplierOrdersMap,
                           Map<String, Pair<List<String>, Integer>> customerOrdersMap,
                           Map<String, Pair<List<String>, Pair<Integer, Double>>> supplierPaymentsMap,
                           Map<String, Pair<List<String>, Pair<Integer, Double>>> customerPaymentsMap,
                           Map<String, Double> godownAggregate,
                           double paymentsSupplier,
                           double paymentsCustomer,
                           double paymentsTotal,
                           double ordersSupplier,
                           double ordersCustomer,
                           double ordersTotal) {

        this.supplierNames = new ArrayList<>(supplierNames);
        this.customerNames = new ArrayList<>(customerNames);
        this.newCustomers = new ArrayList<>(newCustomers);
        this.newSuppliers = new ArrayList<>(newSuppliers);
        this.supplierOrdersMap = new HashMap<>(supplierOrdersMap);
        this.customerOrdersMap = new HashMap<>(customerOrdersMap);
        this.supplierPaymentsMap = new HashMap<>(supplierPaymentsMap);
        this.customerPaymentsMap = new HashMap<>(customerPaymentsMap);
        this.godownAggregate = new HashMap<>(godownAggregate);
        this.paymentsSupplier = paymentsSupplier;
        this.paymentsCustomer = paymentsCustomer;
        this.paymentsTotal = paymentsTotal;
        this.ordersSupplier = ordersSupplier;
        this.ordersCustomer = ordersCustomer;
        this.ordersTotal = ordersTotal;
    }
}
