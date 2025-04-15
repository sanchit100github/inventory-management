package com.example.inventory_management.controller;

import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory_management.model.AuditLog;
import com.example.inventory_management.model.User;
import com.example.inventory_management.model.Role;
import com.example.inventory_management.model.SupplierOrder;
import com.example.inventory_management.model.CustomerOrder;
import com.example.inventory_management.model.Payment;
import com.example.inventory_management.model.Refill;
import com.example.inventory_management.repository.AuditLogRepository;
import com.example.inventory_management.repository.RoleRepository;
import com.example.inventory_management.repository.UserRepository;
import com.example.inventory_management.service.AuditLogService;
import com.example.inventory_management.service.BatchService;
import com.example.inventory_management.service.CustomerOrderService;
import com.example.inventory_management.service.PaymentService;
import com.example.inventory_management.service.ProductService;
import com.example.inventory_management.service.RefillService;
import com.example.inventory_management.service.ReportService;
import com.example.inventory_management.service.SupplierOrderService;
import com.example.inventory_management.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuditLogRepository auditLogRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    UserService userService;

    @Autowired
    BatchService batchService;

    @Autowired
    ProductService productService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    SupplierOrderService supplierOrderService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    RefillService refillService;

    private final ReportService reportService;

    public ManagerController(ReportService reportService) {
        this.reportService = reportService;
    }

    private Optional<User> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        return user;
    }

    @GetMapping("/getemployeeroles")
    public ResponseEntity<?> getRoles() {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                List<Role> roles = userService.getReqRolesEmployee(user.get());
                return new ResponseEntity<>(roles, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/addcategories")
    public ResponseEntity<?> addCategories(@RequestBody Role role) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                try {
                    if (roleRepository.findByName("EMPLOYEE_" + role.getName()).isPresent()) {
                        return new ResponseEntity<>("Category already exists", HttpStatus.CONFLICT);
                    } else {
                        role.setAddedby(user.get().getAssigned());
                        role.setName("EMPLOYEE_" + role.getName());
                        roleRepository.save(role);
                        AuditLog log = new AuditLog(user.get().getEmail(), "ADD", "Added category" + role.getName(),
                                List.of(user.get().getAssigned().getAddedby()));
                        auditLogRepository.save(log);
                        return new ResponseEntity<>("Category is added", HttpStatus.OK);
                    }
                } catch (Exception e) {
                    return new ResponseEntity<>("category is not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/addemployee")
    public ResponseEntity<?> addEmployee(@RequestBody User user1) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                if (userRepository.findByEmailAndActive(user1.getEmail(), true).isPresent()) {
                    return new ResponseEntity<>("Employee with this email already exists", HttpStatus.CONFLICT);
                }
                user1.setAssigned(user.get().getAssigned());
                User test = userService.saveNewUser(user1);
                if (test != null) {
                    AuditLog log = new AuditLog(user.get().getEmail(), "ADD", "Added employee " + user1.getEmail(),
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogService.saveAudit(log);
                    return new ResponseEntity<>("Employee has been added", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Employee was not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/updateEmployee")
    public ResponseEntity<?> updateEmployee(@RequestBody User user1) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                if (!userRepository.findByEmailAndActive(user1.getEmail(), true).isPresent()) {
                    return new ResponseEntity<>("Employee with this email does not exist", HttpStatus.NOT_FOUND);
                }
                User test = userService.updateUser(user1);
                if (test != null) {
                    AuditLog log = new AuditLog(user.get().getEmail(), "UPDATE", "Updated employee " + user1.getEmail(),
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogRepository.save(log);
                    return new ResponseEntity<>("Employee has been updated", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Employee was not updated", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/getemployee")
    public ResponseEntity<?> findEmployee(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                Optional<User> user1 = userService.findUser(email);
                if (user1.isPresent()) {
                    return new ResponseEntity<>(user1, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Employee with this email does not exist", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getallemployees")
    public ResponseEntity<?> findAllEmployees() {
        Optional<User> user = getUser();
        List<User> userList;
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                userList = userService.getAllEmployees(user.get());
                if (userList.isEmpty()) {
                    return new ResponseEntity<>("No employee found", HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(userList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/deleteemployee")
    public ResponseEntity<?> deleteEmployee(@RequestParam String email) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                Optional<User> manager = userRepository.findByEmailAndActive(email, true);
                if (manager.isPresent()) {
                    User managerToUpdate = manager.get();
                    managerToUpdate.setActive(false);
                    userRepository.save(managerToUpdate);

                    AuditLog log = new AuditLog(user.get().getEmail(), "DEACTIVATE",
                            "Deactivated employee " + managerToUpdate.getEmail(),
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogRepository.save(log);

                    return new ResponseEntity<>("Employee deactivated", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/supplierorder/getorders")
    public ResponseEntity<?> getSupplierOrders(@RequestBody Map<String, String> requestBody) {
        String status = requestBody.get("status");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if(user.get().getAssigned().getName().startsWith("MANAGER")) {
                List<SupplierOrder> orders = supplierOrderService.getOrdersByStatus(status, user.get());
                return new ResponseEntity<>(orders, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/supplierorder/addorder")
    public ResponseEntity<?> addSupplierOrder(@RequestBody SupplierOrder order) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                try {
                    order.setOrderedby(user.get().getAssigned());
                    SupplierOrder present = supplierOrderService.saveNewOrder(order);
                    if (present == null) {
                        return new ResponseEntity<>("One or more product is not present", HttpStatus.NOT_FOUND);
                    }
                    AuditLog log = new AuditLog(user.get().getEmail(), "ADD", "Added order " + order.getSupplierId(),
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogService.saveAudit(log);
                    return new ResponseEntity<>("Order added", HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Order was not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/supplierorder/updatestatus")
    public ResponseEntity<?> updateSupplierOrderStatus(@RequestBody Map<String, String> requestBody) {
        String id = requestBody.get("id");
        String status = requestBody.get("status");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                try {
                    Optional<SupplierOrder> order = supplierOrderService.getOrderById(id);
                    if (!order.isPresent()) {
                        return new ResponseEntity<>("Order is not present", HttpStatus.FORBIDDEN);
                    }
                    SupplierOrder present = supplierOrderService.updateOrderStatus(id, status);
                    if (present == null) {
                        return new ResponseEntity<>("One or more product is not present", HttpStatus.NOT_FOUND);
                    }
                    AuditLog log = new AuditLog(user.get().getEmail(), "UPDATE",
                            "Updated order " + supplierOrderService.getOrderById(id) + "status to " + status,
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogService.saveAudit(log);
                    return new ResponseEntity<>("Order status updated", HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Order status was not updated", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/customerorder/add")
    public ResponseEntity<?> addCustomerOrder(@RequestBody CustomerOrder order) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                try {
                    order.setOrderedby(user.get().getAssigned());
                    CustomerOrder present = customerOrderService.placeOrder(order);
                    if (present == null) {
                        return new ResponseEntity<>("Quantity of the one or more product is not sufficient",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                    AuditLog log = new AuditLog(user.get().getEmail(), "ADD", "Added order " + order.getCustomerId(),
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogService.saveAudit(log);
                    return new ResponseEntity<>("Order added", HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Order was not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/customerorder/updatestatus")
    public ResponseEntity<?> updateCustomerOrderStatus(@RequestBody Map<String, String> requestBody) {
        String id = requestBody.get("id");
        String status = requestBody.get("status");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                CustomerOrder present = customerOrderService.updateOrderStatus(id, status);
                if (present == null) {
                    return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
                }
                AuditLog log = new AuditLog(user.get().getEmail(), "UPDATE",
                        "Updated order " + supplierOrderService.getOrderById(id) + "status to " + status,
                        List.of(user.get().getAssigned().getAddedby()));
                auditLogService.saveAudit(log);
                return new ResponseEntity<>("Order status updated", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/customerorder/getorders")
    public ResponseEntity<?> getCustomersOrders(@RequestBody Map<String, String> requestBody) {
        String status = requestBody.get("status");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if(user.get().getAssigned().getName().startsWith("MANAGER")) {
                List<CustomerOrder> orders = customerOrderService.getOrdersByStatus(status, user.get());
                return new ResponseEntity<>(orders, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/payment/getpayment")
    public ResponseEntity<?> getPayment(@RequestBody Map<String, String> requestBody) {
        String id = requestBody.get("id");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                Optional<Payment> payment = paymentService.getPaymentById(id);
                if (payment.isPresent()) {
                    return new ResponseEntity<>(payment, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("No payment is found", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/payment/addpayment")
    public ResponseEntity<?> addPayment(@RequestBody Payment payment) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                try {
                    payment.setAddedby(user.get().getAssigned());
                    Payment present = paymentService.addPayment(payment);
                    if (present == null) {
                        return new ResponseEntity<>("Order for this payment is not present", HttpStatus.NOT_FOUND);
                    }
                    AuditLog log = new AuditLog(user.get().getEmail(), "ADD", "Added payment " + payment.getPaymentId(),
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogService.saveAudit(log);
                    return new ResponseEntity<>("Payment added", HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Payment was not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/payment/updatepayment")
    public ResponseEntity<?> updatePayment(@RequestBody Payment payment) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                try {
                    Payment present = paymentService.updatePayment(payment);
                    if (present == null) {
                        return new ResponseEntity<>("Payment is not present", HttpStatus.NOT_FOUND);
                    }
                    AuditLog log = new AuditLog(user.get().getEmail(), "UPDATE",
                            "Updated payment " + payment.getPaymentId(),
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogService.saveAudit(log);
                    return new ResponseEntity<>("Payment updated", HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Payment was not updated", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/refill/getrefills")
    public ResponseEntity<?> getRefillsByStatus(@RequestBody Map<String, String> requestBody) {
        String status = requestBody.get("status");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                return new ResponseEntity<>(refillService.getRefillByStatus(status, user.get().getAssigned()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/refill/updaterefill")
    public ResponseEntity<?> updateRefill(@RequestBody Refill refill) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                if (refillService.updateRefill(refill) != null) {
                    return new ResponseEntity<>("Refill is updated", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Refill is not updated", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/generatereport")
    public ResponseEntity<?> generateManagerReport(@RequestBody Map<String, Integer> requestBody,
            HttpServletResponse response) throws IOException {
        Optional<User> user = getUser();
        Integer month = requestBody.get("month");
        Integer year = requestBody.get("year");
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("MANAGER")) {
                reportService.generateManagerReport(user.get().getAssigned().getName().replaceFirst("^MANAGER", ""),
                        month, year, response);
                return new ResponseEntity<>("Report is generated", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

}
