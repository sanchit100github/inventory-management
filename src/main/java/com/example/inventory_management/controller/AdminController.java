package com.example.inventory_management.controller;

import java.io.IOException;
import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.inventory_management.model.AuditLog;
import com.example.inventory_management.model.Customer;
import com.example.inventory_management.model.User;
import com.example.inventory_management.model.Role;
import com.example.inventory_management.model.Supplier;
import com.example.inventory_management.repository.AuditLogRepository;
import com.example.inventory_management.repository.RoleRepository;
import com.example.inventory_management.repository.UserRepository;
import com.example.inventory_management.service.AuditLogService;
import com.example.inventory_management.service.SupplierService;
import com.example.inventory_management.service.CustomerService;
import com.example.inventory_management.service.ReportService;
import com.example.inventory_management.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = { "http://localhost:5173",
        "http://127.0.0.1:5173" }, allowedHeaders = "*", allowCredentials = "true", methods = { RequestMethod.GET,
                RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS })
@RestController
@RequestMapping("/admin")
public class AdminController {

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
    SupplierService supplierService;

    @Autowired
    CustomerService customerService;

    private final ReportService reportService;

    public AdminController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/testadmin")
    public ResponseEntity<?> testAdmin() {
        return new ResponseEntity<>("Test is successful", HttpStatus.OK);
    }

    private Optional<User> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        return user;
    }

    @GetMapping("/getmanagerroles")
    public ResponseEntity<?> getRoles() {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().equals("ADMIN")) {
                try {
                    // Get fresh admin role data
                    Role adminRole = roleRepository.findById(user.get().getAssigned().getId())
                            .orElseThrow(() -> new RuntimeException("Admin role not found"));
                    System.out.println("Admin Role ID: " + adminRole.getId());
                    System.out.println("Admin Owned Roles: "
                            + (adminRole.getOwned() != null ? adminRole.getOwned().size() : "null"));

                    // Get all roles that start with MANAGER_
                    List<Role> allRoles = roleRepository.findAll();
                    System.out.println("Total roles in database: " + allRoles.size());

                    List<Role> managerRoles = allRoles.stream()
                            .filter(role -> role.getName().startsWith("MANAGER_"))
                            .toList();
                    System.out.println("Filtered manager roles: " + managerRoles.size());
                    System.out.println("Manager roles: " + managerRoles.stream()
                            .map(Role::getName)
                            .toList());

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("data", managerRoles);
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } catch (Exception e) {
                    e.printStackTrace();
                    Map<String, String> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "Error fetching roles: " + e.getMessage());
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Access denied");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        }
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Access denied");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/adminprofile")
    public ResponseEntity<?> getProfile() {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().equals("ADMIN")) {
                List<Role> roles = userService.getReqRoles(user.get());
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
            if (user.get().getAssigned().getName().equals("ADMIN")) {
                try {
                    // Clean and format the role name
                    String cleanName = role.getName().trim().toUpperCase();
                    String roleName = cleanName.startsWith("MANAGER_") ? cleanName : "MANAGER_" + cleanName;

                    // Check if role exists
                    Optional<Role> existingRole = roleRepository.findByName(roleName);
                    if (existingRole.isPresent()) {
                        Map<String, String> response = new HashMap<>();
                        response.put("status", "error");
                        response.put("message", "Category '" + cleanName + "' already exists");
                        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
                    }

                    // Get fresh admin role
                    Role adminRole = roleRepository.findById(user.get().getAssigned().getId())
                            .orElseThrow(() -> new RuntimeException("Admin role not found"));

                    // Create and save the new role
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    newRole.setDescription(role.getDescription());
                    newRole.setAddedby(adminRole);
                    newRole = roleRepository.save(newRole);

                    // Update admin's owned roles list
                    if (adminRole.getOwned() == null) {
                        adminRole.setOwned(new ArrayList<>());
                    }
                    adminRole.getOwned().add(newRole);
                    adminRole = roleRepository.save(adminRole);

                    // Update the user's assigned role to the latest version
                    User currentUser = user.get();
                    currentUser.setAssigned(adminRole);
                    userRepository.save(currentUser);

                    // Create audit log
                    AuditLog log = new AuditLog(currentUser.getEmail(), "ADD", "Added category " + cleanName,
                            List.of(adminRole));
                    auditLogRepository.save(log);

                    // Prepare success response
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Category '" + cleanName + "' has been added successfully");
                    response.put("category", newRole);
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } catch (Exception e) {
                    e.printStackTrace();
                    Map<String, String> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "Failed to add category: " + e.getMessage());
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Access denied");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        }
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Access denied");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/addmanager")
    public ResponseEntity<?> addManager(@RequestBody User user1) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().equals("ADMIN")) {
                if (userRepository.findByEmailAndActive(user1.getEmail(), true).isPresent()) {
                    return new ResponseEntity<>("Manager with this email already exists", HttpStatus.CONFLICT);
                }
                boolean test = userService.saveNewUser(user1);
                if (test) {
                    AuditLog log = new AuditLog(user.get().getEmail(), "ADD", "Added manager " + user1.getEmail(),
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogService.saveAudit(log);
                    return new ResponseEntity<>("Manager has been added", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Manager was not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/updatemanager")
    public ResponseEntity<?> updateManager(@RequestBody User user1) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().equals("ADMIN")) {
                if (!userRepository.findByEmailAndActive(user1.getEmail(), true).isPresent()) {
                    return new ResponseEntity<>("Manager with this email does not exist", HttpStatus.NOT_FOUND);
                }
                boolean test = userService.updateUser(user1);
                if (test) {
                    AuditLog log = new AuditLog(user.get().getEmail(), "UPDATE", "Updated manager " + user1.getEmail(),
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogRepository.save(log);
                    return new ResponseEntity<>("Manager has been updated", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Manager was not updated", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getmanager")
    public ResponseEntity<?> findManager(@RequestParam String email) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().equals("ADMIN")) {
                Optional<User> user1 = userService.findUser(email);
                if (user1.isPresent()) {
                    return new ResponseEntity<>(user1, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Manager with this email does not exist", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getallmanagers")
    public ResponseEntity<?> findAllManagers() {
        Optional<User> user = getUser();
        List<User> userList;
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().equals("ADMIN")) {
                userList = userService.getAllByActive(true);
                if (userList.isEmpty()) {
                    return new ResponseEntity<>("No manager found", HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(userList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/deletemanager")
    public ResponseEntity<?> deleteManager(@RequestParam String email) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().equals("ADMIN")) {
                Optional<User> manager = userRepository.findByEmailAndActive(email, true);
                if (manager.isPresent()) {
                    User managerToUpdate = manager.get();
                    managerToUpdate.setActive(false);
                    userRepository.save(managerToUpdate);

                    AuditLog log = new AuditLog(user.get().getEmail(), "DEACTIVATE",
                            "Deactivated manager " + managerToUpdate.getEmail(),
                            List.of(user.get().getAssigned().getAddedby()));
                    auditLogRepository.save(log);

                    return new ResponseEntity<>("Manager deactivated", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Manager not found", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/addsupplier")
    public ResponseEntity<?> addSupplier(@RequestBody Supplier supplier) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                if (!supplierService.getSupplierByName(supplier.getName()).isPresent()) {
                    try {
                        supplier.setAdded(LocalDateTime.now());
                        supplierService.saveSupplier(supplier);
                        AuditLog log = new AuditLog(user.get().getEmail(), "ADD",
                                "Added supplier " + supplier.getName(), List.of(user.get().getAssigned().getAddedby()));
                        auditLogService.saveAudit(log);
                        return new ResponseEntity<>("Supplier added successfully", HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>("Supplier addition failed", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>("Supplier is already present with this name", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/updatesupplier")
    public ResponseEntity<?> updateSupplier(@RequestBody Supplier supplier) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                if (supplierService.getSupplierById(supplier.getSupplierId()).isPresent()) {
                    if (supplierService.getSupplierByName(supplier.getName()).get().getSupplierId()
                            .equals(supplier.getSupplierId())) {
                        return new ResponseEntity<>("Customer with this name already exists", HttpStatus.CONFLICT);
                    }
                    try {
                        supplierService.updateSupplier(supplier);
                        AuditLog log = new AuditLog(user.get().getEmail(), "UPDATE",
                                "Updated supplier " + supplier.getName(),
                                List.of(user.get().getAssigned().getAddedby()));
                        auditLogService.saveAudit(log);
                        return new ResponseEntity<>("Supplier updated successfully", HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>("Supplier updation failed", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>("Supplier is not present", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/deletesupplier")
    public ResponseEntity<?> deleteSupplier(@RequestBody Map<String, String> requestBody) {
        String id = requestBody.get("id");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                if (supplierService.getSupplierById(id).isPresent()) {
                    try {
                        supplierService.deleteSupplier(id);
                        AuditLog log = new AuditLog(user.get().getEmail(), "DEACTIVATED",
                                "Deactivated supplier " + supplierService.getSupplierById(id).get().getName(),
                                List.of(user.get().getAssigned().getAddedby()));
                        auditLogService.saveAudit(log);
                        return new ResponseEntity<>("Supplier deactivated successfully", HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>("Supplier deactivation failed", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>("Supplier is not present", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getsupplierbyname")
    public ResponseEntity<?> getSupplierByName(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("name");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                if (supplierService.getSupplierByName(name).isPresent()) {
                    return new ResponseEntity<>(supplierService.getSupplierByName(name).get(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Supplier is not present", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getsuppliers")
    public ResponseEntity<?> getActiveSuppliers() {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                return new ResponseEntity<>(supplierService.findSuppliersByActive(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/addcustomer")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                if (!customerService.getCustomerByName(customer.getName()).isPresent()) {
                    try {
                        customer.setAdded(LocalDateTime.now());
                        customerService.saveCustomer(customer);
                        AuditLog log = new AuditLog(user.get().getEmail(), "ADD",
                                "Added customer " + customer.getName(), List.of(user.get().getAssigned().getAddedby()));
                        auditLogService.saveAudit(log);
                        return new ResponseEntity<>("Customer added successfully", HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>("Customer addition failed", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>("Customer is already present with this name", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/updatecustomer")
    public ResponseEntity<?> updateCustomer(@RequestBody Customer customer) {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                if (customerService.getCustomerById(customer.getCustomerId()).isPresent()) {
                    if (customerService.getCustomerByName(customer.getName()).get().getCustomerId()
                            .equals(customer.getCustomerId())) {
                        return new ResponseEntity<>("Customer with this name already exists", HttpStatus.CONFLICT);
                    }
                    try {
                        customerService.updateCustomer(customer);
                        AuditLog log = new AuditLog(user.get().getEmail(), "UPDATE",
                                "Updated customer " + customer.getName(),
                                List.of(user.get().getAssigned().getAddedby()));
                        auditLogService.saveAudit(log);
                        return new ResponseEntity<>("Customer updated successfully", HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>("Customer updation failed", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>("Customer is not present", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/deletecustomer")
    public ResponseEntity<?> deleteCustomer(@RequestBody Map<String, String> requestBody) {
        String id = requestBody.get("id");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                if (customerService.getCustomerById(id).isPresent()) {
                    try {
                        customerService.deleteCustomer(id);
                        AuditLog log = new AuditLog(user.get().getEmail(), "DEACTIVATED",
                                "Deactivated customer " + customerService.getCustomerById(id).get().getName(),
                                List.of(user.get().getAssigned().getAddedby()));
                        auditLogService.saveAudit(log);
                        return new ResponseEntity<>("Customer deactivated successfully", HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>("Customer deactivation failed", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>("Customer is not present", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getcustomerbyname")
    public ResponseEntity<?> getCustomerByName(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("name");
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                if (customerService.getCustomerByName(name).isPresent()) {
                    return new ResponseEntity<>(customerService.getCustomerByName(name).get(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Customer is not present", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getcustomers")
    public ResponseEntity<?> getActiveCustomers() {
        Optional<User> user = getUser();
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                return new ResponseEntity<>(customerService.findCustomersByActive(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/generatereport")
    public ResponseEntity<?> generateAdminReport(@RequestBody Map<String, Integer> requestBody,
            HttpServletResponse response) throws IOException {
        Optional<User> user = getUser();
        Integer month = requestBody.get("month");
        Integer year = requestBody.get("year");
        if (user.isPresent()) {
            if (user.get().getAssigned().getName().startsWith("ADMIN")) {
                reportService.generateAdminReport(month, year, response);
                return new ResponseEntity<>("Report is generated", HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

}
