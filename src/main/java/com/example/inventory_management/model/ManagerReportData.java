package com.example.inventory_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;
import org.springframework.data.util.Pair;


@Data
@AllArgsConstructor
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

}
