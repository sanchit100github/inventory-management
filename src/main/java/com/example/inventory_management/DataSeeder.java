// package com.example.inventory_management;

// import com.example.inventory_management.model.*;
// import com.example.inventory_management.repository.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Component;

// import java.util.Arrays;
// import java.util.HashSet;
// import java.util.Set;

// @Component
// public class DataSeeder implements CommandLineRunner {

//     @Autowired
//     private ProductRepository productRepository;

//     @Autowired
//     private SupplierRepository supplierRepository;

//     @Autowired
//     private CustomerRepository customerRepository;

//     @Autowired
//     private OrderRepository orderRepository;

//     @Autowired
//     private PaymentRepository paymentRepository;

//     @Autowired
//     private RoleRepository roleRepository;

//     @Autowired
//     private UserRepository userRepository;

//     @Override
//     public void run(String... args) throws Exception {
//         // Initialize the Admin role and admin user
//         initializeAdminRoleAndUser();

//         // Create and save multiple suppliers
//         Supplier supplier1 = new Supplier();
//         supplier1.setSupplierId("4");
//         supplier1.setName("Stokes-Johnson");
//         supplier1.setContactEmail("danielsbrian@example.com");
//         supplier1.setContactPhone("(748)624-9351");
//         supplier1.setAddress("123 Supplier St, City, Country");

//         Supplier supplier2 = new Supplier();
//         supplier2.setSupplierId("5");
//         supplier2.setName("Tech Solutions");
//         supplier2.setContactEmail("contact@techsolutions.com");
//         supplier2.setContactPhone("(123)555-6789");
//         supplier2.setAddress("456 Tech Ave, Silicon Valley, USA");

//         supplierRepository.saveAll(Arrays.asList(supplier1, supplier2));

//         // Create and save multiple products
//         Product product1 = new Product();
//         product1.setProductId("514");
//         product1.setName("Laptop ABC");
//         product1.setCategory("Electronics");
//         product1.setDescription("High-performance laptop for professional work.");
//         product1.setSupplier(supplier1);
//         product1.setPrice(295.64);
//         product1.setCost(78.97);
//         product1.setStockLevel(59);
//         product1.setReorderLevel(19);
//         product1.setLocation("Aisle 4");
//         product1.setBarcode("9733639121");

//         Product product2 = new Product();
//         product2.setProductId("515");
//         product2.setName("Phone XYZ");
//         product2.setCategory("Electronics");
//         product2.setDescription("Latest model with top-notch features.");
//         product2.setSupplier(supplier1);
//         product2.setPrice(499.99);
//         product2.setCost(250.00);
//         product2.setStockLevel(100);
//         product2.setReorderLevel(30);
//         product2.setLocation("Aisle 2");
//         product2.setBarcode("9733639122");

//         Product product3 = new Product();
//         product3.setProductId("516");
//         product3.setName("Smartwatch 3000");
//         product3.setCategory("Electronics");
//         product3.setDescription("Feature-packed smartwatch with health monitoring.");
//         product3.setSupplier(supplier2);
//         product3.setPrice(129.99);
//         product3.setCost(55.00);
//         product3.setStockLevel(80);
//         product3.setReorderLevel(25);
//         product3.setLocation("Aisle 3");
//         product3.setBarcode("9733639123");

//         Product product4 = new Product();
//         product4.setProductId("517");
//         product4.setName("Wireless Headphones");
//         product4.setCategory("Electronics");
//         product4.setDescription("Noise-canceling wireless headphones.");
//         product4.setSupplier(supplier2);
//         product4.setPrice(79.99);
//         product4.setCost(35.00);
//         product4.setStockLevel(150);
//         product4.setReorderLevel(50);
//         product4.setLocation("Aisle 1");
//         product4.setBarcode("9733639124");

//         productRepository.saveAll(Arrays.asList(product1, product2, product3, product4));

//         // Create and save multiple customers
//         Customer customer1 = new Customer();
//         customer1.setCustomerId("1");
//         customer1.setName("John Doe");
//         customer1.setEmail("john.doe@example.com");
//         customer1.setContact("(555) 123-4567");
//         customer1.setAddress("456 Main St, City, Country");

//         Customer customer2 = new Customer();
//         customer2.setCustomerId("2");
//         customer2.setName("Jane Smith");
//         customer2.setEmail("jane.smith@example.com");
//         customer2.setContact("(555) 987-6543");
//         customer2.setAddress("789 Oak St, City, Country");

//         Customer customer3 = new Customer();
//         customer3.setCustomerId("3");
//         customer3.setName("Sam Wilson");
//         customer3.setEmail("sam.wilson@example.com");
//         customer3.setContact("(555) 234-5678");
//         customer3.setAddress("321 Pine St, City, Country");

//         customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));

//         // Create and save multiple orders
//         Order order1 = new Order();
//         order1.setOrderId("101");
//         order1.setCustomerId("1");
//         order1.setOrderDate("2025-01-01");
//         order1.setStatus("Pending");
//         order1.setTotalAmount(product1.getPrice() * 1 + product2.getPrice() * 2);

//         OrderProduct orderProduct1 = new OrderProduct();
//         orderProduct1.setProductId("514");
//         orderProduct1.setQuantity(1);
//         orderProduct1.setPriceAtOrder(product1.getPrice());

//         OrderProduct orderProduct2 = new OrderProduct();
//         orderProduct2.setProductId("515");
//         orderProduct2.setQuantity(2);
//         orderProduct2.setPriceAtOrder(product2.getPrice());

//         order1.setProducts(Arrays.asList(orderProduct1, orderProduct2));

//         Order order2 = new Order();
//         order2.setOrderId("102");
//         order2.setCustomerId("2");
//         order2.setOrderDate("2025-01-02");
//         order2.setStatus("Shipped");
//         order2.setTotalAmount(product1.getPrice() * 2);

//         OrderProduct orderProduct3 = new OrderProduct();
//         orderProduct3.setProductId("514");
//         orderProduct3.setQuantity(2);
//         orderProduct3.setPriceAtOrder(product1.getPrice());

//         order2.setProducts(Arrays.asList(orderProduct3));

//         Order order3 = new Order();
//         order3.setOrderId("103");
//         order3.setCustomerId("3");
//         order3.setOrderDate("2025-01-03");
//         order3.setStatus("Shipped");
//         order3.setTotalAmount(product3.getPrice() * 3);

//         OrderProduct orderProduct4 = new OrderProduct();
//         orderProduct4.setProductId("516");
//         orderProduct4.setQuantity(3);
//         orderProduct4.setPriceAtOrder(product3.getPrice());

//         order3.setProducts(Arrays.asList(orderProduct4));

//         orderRepository.saveAll(Arrays.asList(order1, order2, order3));

//         // Create and save multiple payments
//         Payment payment1 = new Payment();
//         payment1.setPaymentId("201");
//         payment1.setOrder(order1);
//         payment1.setAmount(order1.getTotalAmount());
//         payment1.setPaymentMethod("Credit Card");
//         payment1.setPaymentStatus("Completed");
//         payment1.setTransactionId("TXN123456");
//         payment1.setPaymentDate("2025-01-01");

//         Payment payment2 = new Payment();
//         payment2.setPaymentId("202");
//         payment2.setOrder(order2);
//         payment2.setAmount(order2.getTotalAmount());
//         payment2.setPaymentMethod("PayPal");
//         payment2.setPaymentStatus("Completed");
//         payment2.setTransactionId("TXN789012");
//         payment2.setPaymentDate("2025-01-02");

//         Payment payment3 = new Payment();
//         payment3.setPaymentId("203");
//         payment3.setOrder(order3);
//         payment3.setAmount(order3.getTotalAmount());
//         payment3.setPaymentMethod("Debit Card");
//         payment3.setPaymentStatus("Completed");
//         payment3.setTransactionId("TXN345678");
//         payment3.setPaymentDate("2025-01-03");

//         paymentRepository.saveAll(Arrays.asList(payment1, payment2, payment3));

//         System.out.println("Sample data inserted into MongoDB!");
//     }

//     private void initializeAdminRoleAndUser() {
//         // Check and create Admin role if not present
//         if (roleRepository.findByName("ADMIN").isEmpty()) {
//             Role adminRole = new Role();
//             adminRole.setName("ADMIN");
//             roleRepository.save(adminRole);
//         }

//         // Check and create Admin user if not present
//         if (userRepository.findByEmailAndActive("admin@domain.com", true).isEmpty()) {
//             User adminUser = new User();
//             adminUser.setEmail("admin@domain.com");
//             adminUser.setPassword(new BCryptPasswordEncoder().encode("adminpassword"));
//             adminUser.setActive(true);
//             adminUser.setContact("841568465");
//             Set<Role> roles = new HashSet<>();
//             roles.add(roleRepository.findByName("ADMIN").get());
//             adminUser.setRoles(roles);
            
//             userRepository.save(adminUser);
//         }
//     }
// }
