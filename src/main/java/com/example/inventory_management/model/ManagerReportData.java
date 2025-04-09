package com.example.inventory_management.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.util.Pair;

@Data
public class ManagerReportData {

    private Map<String, Pair<List<String>, Integer>> supplierOrdersMap;
    private Map<String, Pair<List<String>, Integer>> customerOrdersMap;

    private Map<String, Pair<List<String>, Pair<Integer, Double>>> supplierPaymentsMap;
    private Map<String, Pair<List<String>, Pair<Integer, Double>>> customerPaymentsMap;

    private double customerPaymentsTotal;
    private double supplierPaymentsTotal;
    private double paymentsTotal;
    private double supplierTotal;
    private double customerTotal;
    private double ordersTotal;

    private Map<String, Double> godownAggregate;

    private List<Product> newActiveProducts;
    private List<Product> newDeletedProducts;

    private Product mostOrderedProduct;
    private Product leastOrderedProduct;
    private Product mostProfitableProduct;
    private Product leastProfitableProduct;

    private List<CustomerOrder> cancelledCustomerOrders;
    private List<SupplierOrder> cancelledSupplierOrders;

    public ManagerReportData(
            Map<String, Pair<List<String>, Integer>> supplierOrdersMap,
            Map<String, Pair<List<String>, Integer>> customerOrdersMap,
            Map<String, Pair<List<String>, Pair<Integer, Double>>> supplierPaymentsMap,
            Map<String, Pair<List<String>, Pair<Integer, Double>>> customerPaymentsMap,
            double customerPaymentsTotal,
            double supplierPaymentsTotal,
            double paymentsTotal,
            double supplierTotal,
            double customerTotal,
            double ordersTotal,
            Map<String, Double> godownAggregate,
            List<Product> newActiveProducts,
            List<Product> newDeletedProducts,
            Product mostOrderedProduct,
            Product leastOrderedProduct,
            Product mostProfitableProduct,
            Product leastProfitableProduct,
            List<CustomerOrder> cancelledCustomerOrders,
            List<SupplierOrder> cancelledSupplierOrders) {

        this.supplierOrdersMap = new HashMap<>(supplierOrdersMap);
        this.customerOrdersMap = new HashMap<>(customerOrdersMap);
        this.supplierPaymentsMap = new HashMap<>(supplierPaymentsMap);
        this.customerPaymentsMap = new HashMap<>(customerPaymentsMap);
        this.customerPaymentsTotal = customerPaymentsTotal;
        this.supplierPaymentsTotal = supplierPaymentsTotal;
        this.paymentsTotal = paymentsTotal;
        this.supplierTotal = supplierTotal;
        this.customerTotal = customerTotal;
        this.ordersTotal = ordersTotal;
        this.godownAggregate = new HashMap<>(godownAggregate);
        this.newActiveProducts = new ArrayList<>(newActiveProducts);
        this.newDeletedProducts = new ArrayList<>(newDeletedProducts);
        this.mostOrderedProduct = mostOrderedProduct;
        this.leastOrderedProduct = leastOrderedProduct;
        this.mostProfitableProduct = mostProfitableProduct;
        this.leastProfitableProduct = leastProfitableProduct;
        this.cancelledCustomerOrders = new ArrayList<>(cancelledCustomerOrders);
        this.cancelledSupplierOrders = new ArrayList<>(cancelledSupplierOrders);
    }
}
