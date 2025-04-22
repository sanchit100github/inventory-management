package com.example.inventory_management.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.example.inventory_management.model.Supplier;
import com.example.inventory_management.model.SupplierOrder;
import com.example.inventory_management.model.AdminReportData;
import com.example.inventory_management.model.Customer;
import com.example.inventory_management.model.CustomerOrder;
import com.example.inventory_management.model.ManagerReportData;
import com.example.inventory_management.model.OrderProduct;
import com.example.inventory_management.model.Payment;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.repository.CustomerRepository;
import com.example.inventory_management.repository.PaymentRepository;
import com.example.inventory_management.repository.ProductRepository;
import com.example.inventory_management.repository.SupplierRepository;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ReportService {

    @Autowired
    SupplierService supplierService;

    @Autowired
    CustomerService customerService;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PaymentService paymentService;

    @Autowired
    ProductService productService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    SupplierOrderService supplierOrderService;

    public void generateAdminReport(int month, int year, HttpServletResponse response) throws IOException {

        List<Supplier> suppliers = supplierRepository.findAllByActive(true);
        List<String> supplierNames = new ArrayList<>();
        for(Supplier it : suppliers) {
            supplierNames.add(it.getName());
        }

        List<Customer> customers = customerRepository.findAllByActive(true);
        List<String> customerNames = new ArrayList<>();
        for(Customer it : customers) {
            customerNames.add(it.getName());
        }

        List<String> newCustomers = new ArrayList<>();
        for(Customer it : customers) {
            if(it.getAdded().getMonthValue() == month && it.getAdded().getYear() == year) {
                newCustomers.add(it.getName());
            }
        }

        List<String> newSuppliers = new ArrayList<>();
        for(Supplier it : suppliers) {
            if(it.getAdded().getMonthValue() == month && it.getAdded().getYear() == year) {
                newSuppliers.add(it.getName());
            }
        }

        List<SupplierOrder> supplierOrders = supplierOrderService.findAllByMonthAndYear(month, year);
        List<CustomerOrder> customerOrders = customerOrderService.findAllByMonthAndYear(month, year);

        Map<String, Pair<List<String>, Integer>> supplierOrdersMap = new HashMap<>();
        for(SupplierOrder it : supplierOrders) {

            String name = it.getOrderedby().getName().replaceFirst("^MANAGER_", "");
            supplierOrdersMap.putIfAbsent(name, Pair.of(new ArrayList<>(), 0));

            Pair<List<String>, Integer> pair = supplierOrdersMap.get(name);
            pair.getFirst().add(it.getOrderId());
            supplierOrdersMap.put(name, Pair.of(pair.getFirst(), pair.getFirst().size()));

        }

        Map<String, Pair<List<String>, Integer>> customerOrdersMap = new HashMap<>();
        for(CustomerOrder it : customerOrders) {

            String name = it.getOrderedby().getName().replaceFirst("^MANAGER_", "");
            customerOrdersMap.putIfAbsent(name, Pair.of(new ArrayList<>(), 0));

            Pair<List<String>, Integer> pair = customerOrdersMap.get(name);
            pair.getFirst().add(it.getOrderId());
            customerOrdersMap.put(name, Pair.of(pair.getFirst(), pair.getFirst().size()));

        }

        List<Payment> payments = paymentRepository.findAll();
        List<Payment> supplierPayments = new ArrayList<>();
        List<Payment> customerPayments = new ArrayList<>();

        for(Payment it : payments) {
            if(it.getOrderType().equals("Supplier") && it.getPaymentDate().getMonthValue() == month && it.getPaymentDate().getYear() == year) {
                supplierPayments.add(it);
            }
            else if(it.getOrderType().equals("Customer") && it.getPaymentDate().getMonthValue() == month && it.getPaymentDate().getYear() == year) {
                customerPayments.add(it);
            }
        }

        double customerPaymentsTotal=0;
        double supplierPaymentsTotal=0;

        Map<String, Pair<List<String>, Pair<Integer, Double>>> supplierPaymentsMap = new HashMap<>();
        for (Payment it : supplierPayments) {
            supplierPaymentsTotal+=it.getAmount();
            String name = it.getAddedby().getName().replaceFirst("^MANAGER_", "");
            
            supplierPaymentsMap.putIfAbsent(name, Pair.of(new ArrayList<>(), Pair.of(0, 0.0))); // Fix: Use 0.0 for Double

            Pair<List<String>, Pair<Integer, Double>> pair = supplierPaymentsMap.get(name); // Fix: Correct type
            pair.getFirst().add(it.getPaymentId());

            supplierPaymentsMap.put(name, 
                Pair.of(
                    pair.getFirst(), 
                    Pair.of(pair.getFirst().size(), pair.getSecond().getSecond() + it.getAmount()) // Fix: Double addition
                )
            );
        }

        Map<String, Pair<List<String>, Pair<Integer, Double>>> customerPaymentsMap = new HashMap<>();
        for (Payment it : customerPayments) {
            customerPaymentsTotal+=it.getAmount();
            String name = it.getAddedby().getName().replaceFirst("^MANAGER_", "");
            
            customerPaymentsMap.putIfAbsent(name, Pair.of(new ArrayList<>(), Pair.of(0, 0.0))); // Fix: Use 0.0 for Double

            Pair<List<String>, Pair<Integer, Double>> pair = customerPaymentsMap.get(name); // Fix: Correct type
            pair.getFirst().add(it.getPaymentId());

            customerPaymentsMap.put(name, 
                Pair.of(
                    pair.getFirst(), 
                    Pair.of(pair.getFirst().size(), pair.getSecond().getSecond() + it.getAmount()) // Fix: Double addition
                )
            );
        }

        Map<String, Double> godownAggregate = new HashMap<>();

        for (Map.Entry<String, Pair<List<String>, Pair<Integer, Double>>> entry : supplierPaymentsMap.entrySet()) {
            String godownName = entry.getKey();
            double supplierTotal = entry.getValue().getSecond().getSecond(); // Get total supplier payments
        
            godownAggregate.put(godownName, -supplierTotal); // Supplier payments contribute to loss (-ve)
        }
        
        for (Map.Entry<String, Pair<List<String>, Pair<Integer, Double>>> entry : customerPaymentsMap.entrySet()) {
            String godownName = entry.getKey();
            double customerTotal = entry.getValue().getSecond().getSecond(); // Get total customer payments
        
            // If the godown already exists, update profit/loss; otherwise, just add customer total
            godownAggregate.put(godownName, godownAggregate.getOrDefault(godownName, 0.0) + customerTotal);
        }

        double paymentsTotal = customerPaymentsTotal - supplierPaymentsTotal;

        double supplierTotal=0;
        double customerTotal=0;

        for(SupplierOrder it : supplierOrders) {
            supplierTotal+=it.getTotalAmount();
        }

        for(CustomerOrder it : customerOrders) {
            customerTotal+=it.getTotalAmount();
        }

        double ordersTotal = customerTotal - supplierTotal;

        AdminReportData reportData = new AdminReportData(
            supplierNames, customerNames, newCustomers, newSuppliers,
            supplierOrdersMap, customerOrdersMap, supplierPaymentsMap, customerPaymentsMap,
            godownAggregate, supplierPaymentsTotal, customerPaymentsTotal, paymentsTotal, supplierTotal, customerTotal, ordersTotal
        );
 
        String htmlContent = generateAdminHtmlContent(reportData);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=AdminReport(" + LocalDateTime.now()+").pdf");
        try {
            HtmlConverter.convertToPdf(htmlContent, response.getOutputStream());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }


    private String generateAdminHtmlContent(AdminReportData reportData) {
        StringBuilder htmlContent = new StringBuilder();
    
        htmlContent.append("<html><head><style>");
        htmlContent.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background-color: #f4f6f9; color: #333; }");
        htmlContent.append("h2, h3 { text-align: center; color: #1a1a2e; margin-bottom: 15px; font-weight: 600; }");
        htmlContent.append(".container { max-width: 95%; margin: auto; padding: 30px; background: #ffffff; box-shadow: 0px 4px 12px rgba(0,0,0,0.15); border-radius: 12px; }");
        htmlContent.append("table { width: 100%; border-collapse: collapse; margin: 25px 0; font-size: 15px; }");
        htmlContent.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
        htmlContent.append("th { background-color: #2c3e50; color: white; text-transform: uppercase; letter-spacing: 0.5px; }");
        htmlContent.append("tr:nth-child(even) { background-color: #f8f9fa; }");
        htmlContent.append("tr:hover { background-color: #e9ecef; transition: 0.3s; }");
        htmlContent.append(".summary { background: #f0f3f5; padding: 15px; font-size: 16px; border-radius: 8px; }");
        htmlContent.append("</style></head><body>");
    
        htmlContent.append("<div class='container'>");
        htmlContent.append("<h2>📊 Admin Report</h2>");
    
        // New Customers Table
        htmlContent.append("<h3>👥 New Customers</h3>");
        htmlContent.append("<table><tr><th>Customer Name</th></tr>");
        for (String customer : reportData.getNewCustomers()) {
            htmlContent.append("<tr><td>").append(customer).append("</td></tr>");
        }
        htmlContent.append("</table>");
    
        // New Suppliers Table
        htmlContent.append("<h3>🏭 New Suppliers</h3>");
        htmlContent.append("<table><tr><th>Supplier Name</th></tr>");
        for (String supplier : reportData.getNewSuppliers()) {
            htmlContent.append("<tr><td>").append(supplier).append("</td></tr>");
        }
        htmlContent.append("</table>");
    
        // Godown-wise Customer Orders
        htmlContent.append("<h3>📦 Godown-wise Customer Orders</h3>");
        htmlContent.append("<table><tr><th>Godown</th><th>Orders</th><th>Total Orders</th></tr>");
        for (var entry : reportData.getCustomerOrdersMap().entrySet()) {
            htmlContent.append("<tr><td>").append(entry.getKey()).append("</td>");
            htmlContent.append("<td>").append(String.join(", ", entry.getValue().getFirst())).append("</td>");
            htmlContent.append("<td>").append(entry.getValue().getSecond()).append("</td></tr>");
        }
        htmlContent.append("</table>");
    
        // Godown-wise Supplier Orders
        htmlContent.append("<h3>📜 Godown-wise Supplier Orders</h3>");
        htmlContent.append("<table><tr><th>Godown</th><th>Orders</th><th>Total Orders</th></tr>");
        for (var entry : reportData.getSupplierOrdersMap().entrySet()) {
            htmlContent.append("<tr><td>").append(entry.getKey()).append("</td>");
            htmlContent.append("<td>").append(String.join(", ", entry.getValue().getFirst())).append("</td>");
            htmlContent.append("<td>").append(entry.getValue().getSecond()).append("</td></tr>");
        }
        htmlContent.append("</table>");
    
        // Godown-wise Customer Payments
        htmlContent.append("<h3>💳 Godown-wise Customer Payments</h3>");
        htmlContent.append("<table><tr><th>Godown</th><th>Payments</th><th>Total Payments</th><th>Total Amount (₹)</th></tr>");
        for (var entry : reportData.getCustomerPaymentsMap().entrySet()) {
            htmlContent.append("<tr><td>").append(entry.getKey()).append("</td>");
            htmlContent.append("<td>").append(String.join(", ", entry.getValue().getFirst())).append("</td>");
            htmlContent.append("<td>").append(entry.getValue().getSecond().getFirst()).append("</td>");
            htmlContent.append("<td>₹").append(String.format("%.2f", entry.getValue().getSecond().getSecond())).append("</td></tr>");
        }
        htmlContent.append("</table>");
    
        // Godown-wise Supplier Payments
        htmlContent.append("<h3>💰 Godown-wise Supplier Payments</h3>");
        htmlContent.append("<table><tr><th>Godown</th><th>Payments</th><th>Total Payments</th><th>Total Amount (₹)</th></tr>");
        for (var entry : reportData.getSupplierPaymentsMap().entrySet()) {
            htmlContent.append("<tr><td>").append(entry.getKey()).append("</td>");
            htmlContent.append("<td>").append(String.join(", ", entry.getValue().getFirst())).append("</td>");
            htmlContent.append("<td>").append(entry.getValue().getSecond().getFirst()).append("</td>");
            htmlContent.append("<td>₹").append(String.format("%.2f", entry.getValue().getSecond().getSecond())).append("</td></tr>");
        }
        htmlContent.append("</table>");
    
        // Summary Section
        htmlContent.append("<h3>📌 Summary</h3>");
        htmlContent.append("<div class='summary'><table>");
        htmlContent.append("<tr><th>Total Supplier Payments:</th><td><b>₹").append(String.format("%.2f", reportData.getPaymentsSupplier())).append("</b></td></tr>");
        htmlContent.append("<tr><th>Total Customer Payments:</th><td><b>₹").append(String.format("%.2f", reportData.getPaymentsCustomer())).append("</b></td></tr>");
        htmlContent.append("<tr><th><u>Grand Total Payments:</u></th><td><b>₹").append(String.format("%.2f", reportData.getPaymentsTotal())).append("</b></td></tr>");
        htmlContent.append("<tr><th>Total Supplier Orders Amount:</th><td><b>₹").append(String.format("%.2f", reportData.getOrdersSupplier())).append("</b></td></tr>");
        htmlContent.append("<tr><th>Total Customer Orders Amount:</th><td><b>₹").append(String.format("%.2f", reportData.getOrdersCustomer())).append("</b></td></tr>");
        htmlContent.append("<tr><th><u>Grand Total Order Amount:</u></th><td><b>₹").append(String.format("%.2f", reportData.getOrdersTotal())).append("</b></td></tr>");
        htmlContent.append("</table></div>");
    
        htmlContent.append("</div></body></html>");
        return htmlContent.toString();
    }

    public void generateManagerReport(String role, Integer month, Integer year, HttpServletResponse response) {

        List<SupplierOrder> supplierOrders = supplierOrderService.findAllByMonthAndYearAndRole(role, month, year);
        List<CustomerOrder> customerOrders = customerOrderService.findAllByMonthAndYearAndRole(role, month, year);

        Map<String, Pair<List<String>, Integer>> supplierOrdersMap = new HashMap<>();
        for(SupplierOrder it : supplierOrders) {

            String name = it.getOrderedby().getName().replaceFirst("^MANAGER_", "");
            supplierOrdersMap.putIfAbsent(name, Pair.of(new ArrayList<>(), 0));

            Pair<List<String>, Integer> pair = supplierOrdersMap.get(name);
            pair.getFirst().add(it.getOrderId());
            supplierOrdersMap.put(name, Pair.of(pair.getFirst(), pair.getFirst().size()));

        }

        Map<String, Pair<List<String>, Integer>> customerOrdersMap = new HashMap<>();
        for(CustomerOrder it : customerOrders) {

            String name = it.getOrderedby().getName().replaceFirst("^MANAGER_", "");
            customerOrdersMap.putIfAbsent(name, Pair.of(new ArrayList<>(), 0));

            Pair<List<String>, Integer> pair = customerOrdersMap.get(name);
            pair.getFirst().add(it.getOrderId());
            customerOrdersMap.put(name, Pair.of(pair.getFirst(), pair.getFirst().size()));

        }

        List<Payment> payments = paymentService.findAllByRole(role);
        List<Payment> supplierPayments = new ArrayList<>();
        List<Payment> customerPayments = new ArrayList<>();

        for(Payment it : payments) {
            if(it.getOrderType().equals("Supplier") && it.getPaymentDate().getMonthValue() == month && it.getPaymentDate().getYear() == year) {
                supplierPayments.add(it);
            }
            else if(it.getOrderType().equals("Customer") && it.getPaymentDate().getMonthValue() == month && it.getPaymentDate().getYear() == year) {
                customerPayments.add(it);
            }
        }

        double customerPaymentsTotal=0;
        double supplierPaymentsTotal=0;

        Map<String, Pair<List<String>, Pair<Integer, Double>>> supplierPaymentsMap = new HashMap<>();
        for (Payment it : supplierPayments) {
            supplierPaymentsTotal+=it.getAmount();
            String name = it.getAddedby().getName().replaceFirst("^MANAGER_", "");
            
            supplierPaymentsMap.putIfAbsent(name, Pair.of(new ArrayList<>(), Pair.of(0, 0.0))); // Fix: Use 0.0 for Double

            Pair<List<String>, Pair<Integer, Double>> pair = supplierPaymentsMap.get(name); // Fix: Correct type
            pair.getFirst().add(it.getPaymentId());

            supplierPaymentsMap.put(name, 
                Pair.of(
                    pair.getFirst(), 
                    Pair.of(pair.getFirst().size(), pair.getSecond().getSecond() + it.getAmount()) // Fix: Double addition
                )
            );
        }

        Map<String, Pair<List<String>, Pair<Integer, Double>>> customerPaymentsMap = new HashMap<>();
        for (Payment it : customerPayments) {
            customerPaymentsTotal+=it.getAmount();
            String name = it.getAddedby().getName().replaceFirst("^MANAGER_", "");
            
            customerPaymentsMap.putIfAbsent(name, Pair.of(new ArrayList<>(), Pair.of(0, 0.0))); // Fix: Use 0.0 for Double

            Pair<List<String>, Pair<Integer, Double>> pair = customerPaymentsMap.get(name); // Fix: Correct type
            pair.getFirst().add(it.getPaymentId());

            customerPaymentsMap.put(name, 
                Pair.of(
                    pair.getFirst(), 
                    Pair.of(pair.getFirst().size(), pair.getSecond().getSecond() + it.getAmount()) // Fix: Double addition
                )
            );
        }

        Map<String, Double> godownAggregate = new HashMap<>();

        for (Map.Entry<String, Pair<List<String>, Pair<Integer, Double>>> entry : supplierPaymentsMap.entrySet()) {
            String godownName = entry.getKey();
            double supplierTotal = entry.getValue().getSecond().getSecond(); // Get total supplier payments
        
            godownAggregate.put(godownName, -supplierTotal); // Supplier payments contribute to loss (-ve)
        }
        
        for (Map.Entry<String, Pair<List<String>, Pair<Integer, Double>>> entry : customerPaymentsMap.entrySet()) {
            String godownName = entry.getKey();
            double customerTotal = entry.getValue().getSecond().getSecond(); // Get total customer payments
        
            // If the godown already exists, update profit/loss; otherwise, just add customer total
            godownAggregate.put(godownName, godownAggregate.getOrDefault(godownName, 0.0) + customerTotal);
        }

        double paymentsTotal = customerPaymentsTotal - supplierPaymentsTotal;

        double supplierTotal=0;
        double customerTotal=0;

        for(SupplierOrder it : supplierOrders) {
            supplierTotal+=it.getTotalAmount();
        }

        for(CustomerOrder it : customerOrders) {
            customerTotal+=it.getTotalAmount();
        }

        double ordersTotal = customerTotal - supplierTotal;

        List<Product> newActiveProducts = productService.getByActiveAndMonthAndYearAndRole(role, month, year);
        List<Product> newDeleatedProducts = productService.getByNotActiveAndMonthAndYearAndRole(role, month, year);

        Product mostOrderedProduct = null;
        Product leastOrderedProduct = null;

        Map<String, Integer> productOrderCount = new HashMap<>();

        int maxOrdered=0;
        String maxOrderedProductId = "";

        for(CustomerOrder at : customerOrders) {
            for(OrderProduct it : at.getProducts()) {

                productOrderCount.putIfAbsent(it.getProductId(),  0);

                Integer count = productOrderCount.get(it.getProductId());
                productOrderCount.put(it.getProductId(), count++);
                if(count > maxOrdered) {
                    maxOrderedProductId = it.getProductId();
                }
            }
        }

        int minOrdered=maxOrdered;
        String minOrderedProductId = "";

        for(Map.Entry<String, Integer> entry : productOrderCount.entrySet()) {
            if(minOrdered > entry.getValue()) {
                minOrderedProductId = entry.getKey();
            }
        }

        if(productRepository.findByProductId(maxOrderedProductId).isPresent()) {
            mostOrderedProduct = productRepository.findByProductId(maxOrderedProductId).get();
        }
        if(productRepository.findByProductId(minOrderedProductId).isPresent()) {
            leastOrderedProduct = productRepository.findByProductId(minOrderedProductId).get();
        }

        Product mostProfitableProduct = null;
        Product leastProfitableProduct = null;

        double mostProfitable = 0;

        for(CustomerOrder at : customerOrders) {
            for(OrderProduct it : at.getProducts()) {
                if(it.getPriceAtOrder() - it.getCostAtOrder() > mostProfitable) {
                    mostProfitable = it.getPriceAtOrder() - it.getCostAtOrder();
                    mostProfitableProduct = productRepository.findById(it.getProductId()).get();
                }
            }
        }

        double minProfitable = mostProfitable;

        for(CustomerOrder at : customerOrders) {
            for(OrderProduct it : at.getProducts()) {
                if(it.getPriceAtOrder() - it.getCostAtOrder() < minProfitable) {
                    minProfitable = it.getPriceAtOrder() - it.getCostAtOrder();
                    leastProfitableProduct = productRepository.findById(it.getProductId()).get();
                }
            }
        }

        List<CustomerOrder> cancelledCustomerOrders = new ArrayList<>();
        List<SupplierOrder> cancelledSupplierOrders = new ArrayList<>();

        for(CustomerOrder at : customerOrders) {
            if(at.getStatus().equals("Cancelled")) {
                cancelledCustomerOrders.add(at);
            }
        }

        for(SupplierOrder at : supplierOrders) {
            if(at.getStatus().equals("Cancelled")) {
                cancelledSupplierOrders.add(at);
            }
        }

        ManagerReportData reportData = new ManagerReportData(
            supplierOrdersMap, customerOrdersMap,
            supplierPaymentsMap, customerPaymentsMap,
            customerPaymentsTotal, supplierPaymentsTotal, paymentsTotal,
            supplierTotal, customerTotal, ordersTotal, godownAggregate,
            newActiveProducts, newDeleatedProducts,
            mostOrderedProduct, leastOrderedProduct, mostProfitableProduct, leastProfitableProduct,
            cancelledCustomerOrders, cancelledSupplierOrders
        );

        String htmlContent = generateManagerHtmlContent(reportData);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=AdminReport(" + LocalDateTime.now()+").pdf");
        try {
            HtmlConverter.convertToPdf(htmlContent, response.getOutputStream());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    private String generateManagerHtmlContent(ManagerReportData reportData) {
        StringBuilder htmlContent = new StringBuilder();
    
        htmlContent.append("<html>")
            .append("<head>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; margin: 20px; }")
            .append("h2, h3 { color: #2C3E50; border-bottom: 2px solid #2C3E50; padding-bottom: 5px; }")
            .append("table { width: 100%; border-collapse: collapse; margin-top: 15px; }")
            .append("th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }")
            .append("th { background-color: #2C3E50; color: white; }")
            .append(".highlight { font-weight: bold; color: green; }")
            .append(".negative { color: red; }")
            .append("</style>")
            .append("</head>")
            .append("<body>");
    
        htmlContent.append("<h2>Manager Report</h2>")
            .append("<h3>Summary</h3>")
            .append("<p><strong>Total Customer Payments:</strong> $")
            .append(String.format("%.2f", reportData.getCustomerPaymentsTotal() != null ? reportData.getCustomerPaymentsTotal() : 0.0))
            .append("</p>")
            .append("<p><strong>Total Supplier Payments:</strong> $")
            .append(String.format("%.2f", reportData.getSupplierPaymentsTotal() != null ? reportData.getSupplierPaymentsTotal() : 0.0))
            .append("</p>")
            .append("<p><strong>Net Payments Total:</strong> $")
            .append(String.format("%.2f", reportData.getPaymentsTotal() != null ? reportData.getPaymentsTotal() : 0.0))
            .append("</p>")
            .append("<p><strong>Total Customer Orders:</strong> $")
            .append(String.format("%.2f", reportData.getCustomerTotal() != null ? reportData.getCustomerTotal() : 0.0))
            .append("</p>")
            .append("<p><strong>Total Supplier Orders:</strong> $")
            .append(String.format("%.2f", reportData.getSupplierTotal() != null ? reportData.getSupplierTotal() : 0.0))
            .append("</p>")
            .append("<p><strong>Net Order Profit/Loss:</strong> <span class='")
            .append(reportData.getOrdersTotal() != null && reportData.getOrdersTotal() >= 0 ? "highlight" : "negative")
            .append("'>$")
            .append(String.format("%.2f", reportData.getOrdersTotal() != null ? reportData.getOrdersTotal() : 0.0))
            .append("</span></p>");
    
        htmlContent.append("<h3>Godown Profit/Loss Overview</h3>")
            .append("<table>")
            .append("<tr><th>Godown</th><th>Profit/Loss ($)</th></tr>");
    
        if (reportData.getGodownAggregate() != null) {
            for (Map.Entry<String, Double> entry : reportData.getGodownAggregate().entrySet()) {
                htmlContent.append("<tr>")
                    .append("<td>").append(entry.getKey() != null ? entry.getKey() : "").append("</td>")
                    .append("<td class='")
                    .append(entry.getValue() != null && entry.getValue() >= 0 ? "highlight" : "negative")
                    .append("'>$")
                    .append(String.format("%.2f", entry.getValue() != null ? entry.getValue() : 0.0))
                    .append("</td>")
                    .append("</tr>");
            }
        }
        htmlContent.append("</table>");
    
        htmlContent.append("<h3>Most Ordered & Least Ordered Products</h3>")
            .append("<p><strong>Most Ordered:</strong> ")
            .append(reportData.getMostOrderedProduct() != null && reportData.getMostOrderedProduct().getName() != null 
                    ? reportData.getMostOrderedProduct().getName() : "")
            .append(" (ID: ")
            .append(reportData.getMostOrderedProduct() != null && reportData.getMostOrderedProduct().getProductId() != null 
                    ? reportData.getMostOrderedProduct().getProductId() : "")
            .append(")</p>")
            .append("<p><strong>Least Ordered:</strong> ")
            .append(reportData.getLeastOrderedProduct() != null && reportData.getLeastOrderedProduct().getName() != null 
                    ? reportData.getLeastOrderedProduct().getName() : "")
            .append(" (ID: ")
            .append(reportData.getLeastOrderedProduct() != null && reportData.getLeastOrderedProduct().getProductId() != null 
                    ? reportData.getLeastOrderedProduct().getProductId() : "")
            .append(")</p>");
    
        htmlContent.append("<h3>Most Profitable & Least Profitable Products</h3>")
            .append("<p><strong>Most Profitable:</strong> ")
            .append(reportData.getMostProfitableProduct() != null && reportData.getMostProfitableProduct().getName() != null 
                    ? reportData.getMostProfitableProduct().getName() : "")
            .append("</p>")
            .append("<p><strong>Least Profitable:</strong> ")
            .append(reportData.getLeastProfitableProduct() != null && reportData.getLeastProfitableProduct().getName() != null 
                    ? reportData.getLeastProfitableProduct().getName() : "")
            .append("</p>");
    
        htmlContent.append("<h3>New & Deleted Products</h3>")
            .append("<p><strong>New Active Products:</strong> ")
            .append(reportData.getNewActiveProducts() != null ? reportData.getNewActiveProducts().size() : 0)
            .append("</p>")
            .append("<p><strong>Deleted Products:</strong> ")
            .append(reportData.getNewDeletedProducts() != null ? reportData.getNewDeletedProducts().size() : 0)
            .append("</p>");
    
        htmlContent.append("<h3>Cancelled Orders</h3>")
            .append("<p><strong>Cancelled Customer Orders:</strong> ")
            .append(reportData.getCancelledCustomerOrders() != null ? reportData.getCancelledCustomerOrders().size() : 0)
            .append("</p>")
            .append("<p><strong>Cancelled Supplier Orders:</strong> ")
            .append(reportData.getCancelledSupplierOrders() != null ? reportData.getCancelledSupplierOrders().size() : 0)
            .append("</p>");
    
        htmlContent.append("</body></html>");
    
        return htmlContent.toString();
    }
    
    
    
}    