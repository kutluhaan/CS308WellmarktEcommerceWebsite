package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;


import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Customer;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Order;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CustomerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.OrderServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;
import org.springframework.web.bind.annotation.RequestMapping;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.EmailServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.AuthServices;



import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


import java.util.Map;
import java.util.HashMap;



@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private final OrderServices orderServices;
    @Autowired private final JwtService jwtService;
    @Autowired private final ProductServices productServices;
    @Autowired private final EmailServices emailServices;
    @Autowired private final CustomerServices customerServices;
    @Autowired private final AuthServices authServices;

    @Autowired
    public OrderController(CustomerServices customerServices, OrderServices orderServices, JwtService jwtService, ProductServices productServices, EmailServices emailServices, AuthServices authServices) {
        this.customerServices = customerServices;
        this.orderServices = orderServices;
        this.jwtService = jwtService;
        this.productServices = productServices;
        this.emailServices = emailServices;
        this.authServices = authServices;
    }

    // Tüm orderları döndüren endpoint
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Customer> customers = customerServices.findAllCustomers(); // Tüm müşterileri al
        List<Order> allOrders = new ArrayList<>();

        // Tüm müşterilerin siparişlerini listeye ekle
        for (Customer customer : customers) {
            if (customer.getOrders() != null) {
                allOrders.addAll(customer.getOrders());
            }
        }


        return ResponseEntity.status(HttpStatus.OK).body(allOrders);
    }

    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String productId,
            @RequestParam String newStatus) {

        List<Customer> customers = customerServices.findAllCustomers();

        for (Customer customer : customers) {
            List<Order> orders = customer.getOrders();
            if (orders != null) {
                for (Order order : orders) {
                    if (order.getOrderId().equals(orderId)) {

                        // If order type is "canceled", no updates allowed
                        if ("canceled".equalsIgnoreCase(order.getOrderType())) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Bu sipariş iptal edildi. Durum güncellemesi yapılamaz.");
                        }

                        for (Order.ProductQuantity pq : order.getProducts()) {
                            if (pq.getProductId().equals(productId)) {

                                String orderType = order.getOrderType().toLowerCase();
                                String currentStatus = pq.getStatus();
                                String desiredStatus = newStatus.trim();

                                // ---------------------
                                // ORDER TYPE: "return"
                                // ---------------------
                                if ("return".equals(orderType)) {
                                    // Allowed: "On Process", "Refund Accepted"
                                    if (!desiredStatus.equalsIgnoreCase("On Process")
                                            && !desiredStatus.equalsIgnoreCase("Refund Accepted")) {
                                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body("Return tipindeki sipariş için geçersiz durum: " + desiredStatus);
                                    }

                                    // If current status is "Refund Accepted", do not revert to "On Process"
                                    if ("Refund Accepted".equalsIgnoreCase(currentStatus)
                                            && "On Process".equalsIgnoreCase(desiredStatus)) {
                                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body("Sipariş durumu 'Refund Accepted' seviyesinden geri alınamaz.");
                                    }

                                    // If transitioning to "Refund Accepted", increment stock
                                    if ("Refund Accepted".equalsIgnoreCase(desiredStatus)
                                            && !"Refund Accepted".equalsIgnoreCase(currentStatus)) {
                                        try {
                                            // Get current stock
                                            Product dbProduct = productServices.findProductById(productId);
                                            int updatedStock = dbProduct.getStock() + pq.getQuantity();
                                            // Call your existing updateStock method
                                            productServices.updateStock(productId, updatedStock);

                                            String userEmail = customer.getEmail();
                                            String subject = "Your return request has been approved!";

                                            String productName = dbProduct.getName();           // Ürün adı DB'den
                                            int refundQuantity = pq.getQuantity();              // Kaç adet iade ediliyor
                                            double purchasePrice = pq.getPurchasePrice();       // Birim fiyat (siparişteki fiyat)
                                            double totalRefund = purchasePrice * refundQuantity; // Toplam iade tutarı

                                            // E-posta gövdesini Türkçe veya İngilizce oluşturabilirsiniz; örneğin:
                                            String body = "Merhaba " + customer.getFirstName() + ",\n\n"
                                                    + "Talebiniz olan \"" + productName + "\" (" + refundQuantity + " adet) ürünü için iade isteğiniz kabul edildi.\n"
                                                    + "Toplam " + totalRefund + " TL tarafınıza iade edilmiştir.\n\n"
                                                    + "İyi günler dileriz,\nWellmarkt CS308 Ekibi";

                                            //System.out.println(body);

                                            emailServices.sendEmail(userEmail, subject, body);

                                        } catch (Exception e) {
                                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                    .body("Stok güncellenirken hata oluştu: " + e.getMessage());
                                        }
                                    }

                                    // Update the product status
                                    pq.setStatus(desiredStatus);
                                    customerServices.saveCustomer(customer);
                                    return ResponseEntity.ok(
                                            "Return siparişinin durumu güncellendi: " + desiredStatus);
                                }

                                // -----------------------------
                                // ORDER TYPE: "customer_order"
                                // -----------------------------
                                else if ("customer_order".equals(orderType)) {
                                    // Allowed: "On Process", "In Transit", "Delivered"
                                    List<String> allowedStatuses = List.of("On Process", "In Transit", "Delivered");
                                    boolean isValid = allowedStatuses.stream()
                                            .anyMatch(s -> s.equalsIgnoreCase(desiredStatus));

                                    if (!isValid) {
                                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body("customer_order için geçersiz durum: " + desiredStatus);
                                    }

                                    pq.setStatus(desiredStatus);
                                    customerServices.saveCustomer(customer);
                                    return ResponseEntity.ok(
                                            "customer_order siparişinin durumu güncellendi: " + desiredStatus);
                                }

                                // ---------------------------------------------
                                // OTHER ORDER TYPES (if any)
                                // ---------------------------------------------
                                else {
                                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                            .body("Bilinmeyen orderType: " + order.getOrderType());
                                }
                            }
                        }
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Order bulundu ancak bu productId yok: " + productId);
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Sipariş bulunamadı. orderId: " + orderId);
    }




    @PostMapping("/request-return")
    public ResponseEntity<String> requestReturn(
            @RequestParam String orderId,
            @RequestParam String productId,
            @RequestParam int quantity) {

        // 1. Find the customer and the specific order that matches orderId
        List<Customer> customers = customerServices.findAllCustomers();
        Customer targetCustomer = null;
        Order originalOrder = null;
        Order.ProductQuantity originalProductQuantity = null;

        for (Customer customer : customers) {
            if (customer.getOrders() != null) {
                for (Order order : customer.getOrders()) {
                    if (order.getOrderId().equals(orderId)) {
                        targetCustomer = customer;
                        originalOrder = order;
                        // Find the product in this order
                        for (Order.ProductQuantity product : order.getProducts()) {
                            if (product.getProductId().equals(productId)) {
                                originalProductQuantity = product;
                                break;
                            }
                        }
                        break; // Break out of the orders loop
                    }
                }
            }
            if (originalOrder != null) {
                break; // Break out of the customers loop
            }
        }

        // Validations for the specific product:
        if (originalOrder == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Geçersiz orderId. Böyle bir sipariş yok.");
        }

        if (originalProductQuantity == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Geçersiz productId. Siparişte bu ürün yok.");
        }

        // Check if requested quantity <= purchased quantity
        if (quantity <= 0 || quantity > originalProductQuantity.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("İade talebi için geçersiz miktar.");
        }

        // Check if the product is "On Process" or "Delivered"
        if (!"Delivered".equalsIgnoreCase(originalProductQuantity.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ürün 'Delivered' değil. İade talebi yapılamaz.");
        }

        // Check the purchasedAt date is within 30 days
        LocalDateTime purchasedAt = originalProductQuantity.getPurchasedAt();
        if (purchasedAt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Orijinal satın alma tarihi bulunamadı.");
        }
        long daysBetween = ChronoUnit.DAYS.between(purchasedAt, LocalDateTime.now());
        if (daysBetween > 30) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bu ürünün iade süresi (30 gün) dolmuş.");
        }

        // Check if the requested refund quantity exceeds the purchased quantity
        int alreadyRequested = 0;
        for (Order order : targetCustomer.getOrders()) {
            if ("return".equalsIgnoreCase(order.getOrderType()) && orderId.equals(order.getOldOrderId())) {
                for (Order.ProductQuantity p : order.getProducts()) {
                    if (p.getProductId().equals(productId)) {
                        alreadyRequested += p.getQuantity();
                    }
                }
            }
        }

        if (alreadyRequested + quantity > originalProductQuantity.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Toplam iade talebi, satın alınan miktarı aşıyor. Halihazırda " + alreadyRequested + " adet iade talebinde bulundunuz. "
                            + "Yeni talep (" + quantity + ") ile toplam " + (alreadyRequested + quantity)
                            + " olacak, ancak satın alınan miktar: " + originalProductQuantity.getQuantity());
        }

        // Create a new "return" order object for the specific product
        List<Order.ProductQuantity> returnProducts = new ArrayList<>();
        Order.ProductQuantity returnProduct = new Order.ProductQuantity(originalProductQuantity.getProductId(), quantity);

        // Preserve the original purchase price and date
        returnProduct.setPurchasePrice(originalProductQuantity.getPurchasePrice());
        returnProduct.setPurchasedAt(originalProductQuantity.getPurchasedAt());

        // Set status for the return item
        returnProduct.setStatus("Return Requested");
        returnProducts.add(returnProduct);

        Order returnOrder = new Order(targetCustomer.getcID(), returnProducts);
        returnOrder.setOrderType("return");
        returnOrder.setOldOrderId(orderId);

        // Add this new return order to the same customer’s orders
        if (targetCustomer.getOrders() == null) {
            targetCustomer.setOrders(new ArrayList<>());
        }
        targetCustomer.getOrders().add(returnOrder);

        // Save the customer so the new order is persisted
        customerServices.saveCustomer(targetCustomer);

        return ResponseEntity.status(HttpStatus.OK)
                .body("İade talebi başarıyla oluşturuldu. Yeni iade siparişi 'return' olarak kaydedildi.");
    }




    @PutMapping("/cancel-order/{orderId}")
    public ResponseEntity<String> cancelWholeOrder(@PathVariable String orderId) {
        // 1. Fetch all customers
        List<Customer> customers = customerServices.findAllCustomers();

        // 2. Search through each customer's orders for the given orderId
        for (Customer customer : customers) {
            List<Order> orders = customer.getOrders();
            if (orders != null) {
                for (Order order : orders) {
                    if (order.getOrderId().equals(orderId)) {
                        // Found the matching order

                        // NEW REQUIREMENT: Only allow cancel if orderType == "customer_order"
                        if (!"customer_order".equalsIgnoreCase(order.getOrderType())) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Bu sipariş tipi iptal edilemez. Sadece 'customer_order' iptal edilebilir.");
                        }

                        // 3. Check if every product in this order has status == "On Process"
                        boolean allOnProcess = true;
                        for (Order.ProductQuantity productQty : order.getProducts()) {
                            if (!"On Process".equalsIgnoreCase(productQty.getStatus())) {
                                allOnProcess = false;
                                break;
                            }
                        }

                        // 4. If all products are "On Process", we can cancel
                        if (allOnProcess) {
                            try {
                                // (A) Restock the products & calculate total refund
                                double totalRefund = 0.0;

                                for (Order.ProductQuantity productQty : order.getProducts()) {
                                    // i. Increase product stock in DB
                                    Product dbProduct = productServices.findProductById(productQty.getProductId());
                                    int updatedStock = dbProduct.getStock() + productQty.getQuantity();
                                    productServices.updateStock(dbProduct.getpID(), updatedStock);

                                    // ii. Calculate partial refund for this product
                                    double itemCost = productQty.getPurchasePrice() * productQty.getQuantity();
                                    totalRefund += itemCost;
                                }

                                // (B) Change the order type to "canceled"
                                order.setOrderType("canceled");
                                customerServices.saveCustomer(customer);

                                // (C) Send email to the customer about the refund
                                String userEmail = customer.getEmail();
                                String subject = "Your entire order has been canceled";
                                String body = "Hello " + customer.getFirstName() + ",\n\n"
                                        + "Your order (" + orderId + ") has been canceled successfully.\n"
                                        + "A total refund of " + totalRefund + "₺ has been issued to your account.\n\n"
                                        + "Best regards,\nWellmarkt CS308 Team";

                                emailServices.sendEmail(userEmail, subject, body);

                                return ResponseEntity.status(HttpStatus.OK)
                                        .body("Sipariş başarıyla iptal edildi. Toplam iade: " + totalRefund + "₺");

                            } catch (Exception e) {
                                e.printStackTrace();
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Sipariş iptal edilirken bir hata oluştu: " + e.getMessage());
                            }
                        } else {
                            // If at least one product is not "On Process", we cannot cancel
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Siparişi iptal edebilmek için tüm ürünlerin durumu 'On Process' olmalı.");
                        }
                    }
                }
            }
        }

        // 5. If we never found an order with the given ID
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Verilen orderId'ye sahip bir sipariş bulunamadı.");
    }



    @GetMapping("/my-orders")
    public ResponseEntity<?> getUserOrders(HttpServletRequest request) {
        try {
            // 1. Extract and validate the token
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token eksik veya geçersiz.");
            }
            token = token.substring(7); // remove "Bearer "

            // 2. Get customer ID from token
            String customerId = jwtService.getIDFromToken(token);

            // 3. Retrieve customer from DB
            Customer customer = customerServices.findById(customerId);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Kullanıcı bulunamadı.");
            }
            // 4. Get the orders
            List<Order> orders = customer.getOrders();
            if (orders == null) {
                orders = new ArrayList<>();
            }

            // 5. Transform orders into the desired JSON structure
            List<Map<String, Object>> responseList = new ArrayList<>();

            for (Order order : orders) {
                Map<String, Object> orderData = new HashMap<>();

                // a) orderId
                orderData.put("orderId", order.getOrderId());

                // b) purchased_at (we use the purchasedAt of the FIRST product, or null if empty)
                LocalDateTime purchasedAt = null;
                if (!order.getProducts().isEmpty()) {
                    purchasedAt = order.getProducts().get(0).getPurchasedAt();
                }
                orderData.put("purchased_at", purchasedAt);

                // c) Build the products array
                List<Map<String, Object>> productList = new ArrayList<>();
                for (Order.ProductQuantity pq : order.getProducts()) {
                    Map<String, Object> productData = new HashMap<>();

                    // Fetch full product info from DB to get name, photo, etc.
                    Product dbProduct = productServices.findProductById(pq.getProductId());

                    productData.put("product_name", dbProduct.getName());
                    productData.put("product_id", pq.getProductId());
                    productData.put("product_photo", dbProduct.getImageURL());
                    productData.put("quantity", pq.getQuantity());
                    productData.put("status", pq.getStatus());

                    // total price = purchasePrice * quantity
                    double totalPrice = pq.getPurchasePrice() * pq.getQuantity();
                    productData.put("total_price", totalPrice);

                    productList.add(productData);
                }
                orderData.put("products", productList);

                // d) orderType
                orderData.put("orderType", order.getOrderType());

                orderData.put("address", order.getAddress());

                responseList.add(orderData);
            }

            // 6. Return the transformed list
            return ResponseEntity.status(HttpStatus.OK).body(responseList);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Beklenmedik bir hata oluştu.");
        }
    }

    //@PreAuthorize("hasRole('salesManager')")
    @GetMapping("/all-orders-alltime")
    public ResponseEntity<List<Map<String, Object>>> getAllOrdersAllTime() {
        // 1. Retrieve all customers
        List<Customer> customers = customerServices.findAllCustomers();

        // 2. Prepare a list to hold all product purchase data
        List<Map<String, Object>> allPurchases = new ArrayList<>();

        // 3. Loop through each customer -> order -> productQuantity
        for (Customer customer : customers) {
            List<Order> orders = customer.getOrders();
            if (orders != null) {
                for (Order order : orders) {
                    List<Order.ProductQuantity> products = order.getProducts();
                    if (products != null) {
                        for (Order.ProductQuantity pq : products) {
                            // Build a map for each purchased item
                            Map<String, Object> itemData = new HashMap<>();
                            itemData.put("productId", pq.getProductId());
                            itemData.put("quantity", pq.getQuantity());
                            itemData.put("purchasePrice", pq.getPurchasePrice());
                            itemData.put("purchasedAt", pq.getPurchasedAt());

                            // You can store `purchasedAt` as a String if you prefer:
                            // itemData.put("purchasedAt", pq.getPurchasedAt().toString());

                            allPurchases.add(itemData);
                        }
                    }
                }
            }
        }

        // 4. Return the aggregated list
        return ResponseEntity.ok(allPurchases);
    }


    //@PreAuthorize("hasRole('salesManager')")
    @GetMapping("/plus-orders-alltime")
    public ResponseEntity<List<Map<String, Object>>> getPlusOrdersAllTime() {
        // Fetch all customers
        List<Customer> customers = customerServices.findAllCustomers();

        // Prepare a list to hold filtered purchased items
        List<Map<String, Object>> filteredPurchases = new ArrayList<>();

        // Loop through each customer -> order -> productQuantity
        for (Customer customer : customers) {
            List<Order> orders = customer.getOrders();
            if (orders != null) {
                for (Order order : orders) {
                    String orderType = order.getOrderType();
                    if (orderType == null) {
                        continue; // skip if no orderType
                    }

                    // 1) If orderType == "customer_order", add ALL products
                    if (orderType.equalsIgnoreCase("customer_order")) {
                        for (Order.ProductQuantity pq : order.getProducts()) {
                            Map<String, Object> itemData = new HashMap<>();
                            itemData.put("productId", pq.getProductId());
                            itemData.put("quantity", pq.getQuantity());
                            itemData.put("purchasePrice", pq.getPurchasePrice());
                            itemData.put("purchasedAt", pq.getPurchasedAt());
                            filteredPurchases.add(itemData);
                        }
                    }
                    // 2) If orderType == "return", only add products with status "On Process"
                    else if (orderType.equalsIgnoreCase("return")) {
                        for (Order.ProductQuantity pq : order.getProducts()) {
                            if ("On Process".equalsIgnoreCase(pq.getStatus())) {
                                Map<String, Object> itemData = new HashMap<>();
                                itemData.put("productId", pq.getProductId());
                                itemData.put("quantity", pq.getQuantity());
                                itemData.put("purchasePrice", pq.getPurchasePrice());
                                itemData.put("purchasedAt", pq.getPurchasedAt());
                                filteredPurchases.add(itemData);
                            }
                        }
                    }
                }
            }
        }

        // Return the filtered list
        return ResponseEntity.ok(filteredPurchases);
    }

    //@PreAuthorize("hasRole('salesManager')")
    @GetMapping("/minus-orders-alltime")
    public ResponseEntity<List<Map<String, Object>>> getMinusOrdersAllTime() {
        // 1. Get all customers
        List<Customer> customers = customerServices.findAllCustomers();
        // 2. Prepare a list to hold the filtered product entries
        List<Map<String, Object>> filteredPurchases = new ArrayList<>();

        // 3. Traverse all customers -> orders -> products
        for (Customer customer : customers) {
            if (customer.getOrders() != null) {
                for (Order order : customer.getOrders()) {
                    String orderType = order.getOrderType();
                    if (orderType == null) {
                        continue; // Skip if no orderType
                    }

                    // CASE A: orderType == "return"
                    if (orderType.equalsIgnoreCase("return")) {
                        for (Order.ProductQuantity pq : order.getProducts()) {
                            // Only add products if status == "Refund Accepted"
                            if ("Refund Accepted".equalsIgnoreCase(pq.getStatus())) {
                                Map<String, Object> itemData = new HashMap<>();
                                itemData.put("productId", pq.getProductId());
                                itemData.put("quantity", pq.getQuantity());
                                itemData.put("purchasePrice", pq.getPurchasePrice());
                                itemData.put("purchasedAt", pq.getPurchasedAt());
                                filteredPurchases.add(itemData);
                            }
                        }
                    }
                    // CASE B: orderType == "canceled"
                    else if (orderType.equalsIgnoreCase("canceled")) {
                        // Include all products for canceled orders
                        for (Order.ProductQuantity pq : order.getProducts()) {
                            Map<String, Object> itemData = new HashMap<>();
                            itemData.put("productId", pq.getProductId());
                            itemData.put("quantity", pq.getQuantity());
                            itemData.put("purchasePrice", pq.getPurchasePrice());
                            itemData.put("purchasedAt", pq.getPurchasedAt());
                            filteredPurchases.add(itemData);
                        }
                    }
                }
            }
        }

        // 4. Return the filtered results
        return ResponseEntity.ok(filteredPurchases);
    }
    
    //@PreAuthorize("hasRole('salesManager')")
    @GetMapping("/all-orders-time")
    public ResponseEntity<List<Map<String, Object>>> getAllOrdersGivenTime(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        // 1. Parse the incoming date/time strings to LocalDateTime
        //    (Adjust format/parsing as needed based on how you send the dates)
        LocalDateTime startDate;
        LocalDateTime endDate;

        try {
            // Example parse, assuming standard ISO-8601 format like "2024-12-28T00:00:00"
            startDate = LocalDateTime.parse(start);
            endDate = LocalDateTime.parse(end);
        } catch (Exception e) {
            // If there's a parsing error, return a bad request
            return ResponseEntity.badRequest()
                    .body(null);
        }

        // 2. Retrieve all customers
        List<Customer> customers = customerServices.findAllCustomers();

        // 3. Prepare a list to hold all product purchase data
        List<Map<String, Object>> filteredPurchases = new ArrayList<>();

        // 4. Loop through each customer -> order -> productQuantity
        for (Customer customer : customers) {
            List<Order> orders = customer.getOrders();
            if (orders != null) {
                for (Order order : orders) {
                    if (order.getProducts() != null) {
                        for (Order.ProductQuantity pq : order.getProducts()) {
                            // Only include if purchasedAt is within [startDate, endDate]
                            LocalDateTime purchasedAt = pq.getPurchasedAt();
                            if (purchasedAt != null
                                    && (purchasedAt.isAfter(startDate) || purchasedAt.isEqual(startDate))
                                    && (purchasedAt.isBefore(endDate) || purchasedAt.isEqual(endDate))) {

                                Map<String, Object> itemData = new HashMap<>();
                                itemData.put("productId", pq.getProductId());
                                itemData.put("quantity", pq.getQuantity());
                                itemData.put("purchasePrice", pq.getPurchasePrice());
                                itemData.put("purchasedAt", purchasedAt);

                                filteredPurchases.add(itemData);
                            }
                        }
                    }
                }
            }
        }

        // 5. Return the filtered list
        return ResponseEntity.ok(filteredPurchases);
    }

    //@PreAuthorize("hasRole('salesManager')")
    @GetMapping("/plus-orders-time")
    public ResponseEntity<List<Map<String, Object>>> getPlusOrdersGivenTime(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        // 1. Parse date/time from the input query parameters
        LocalDateTime startDate;
        LocalDateTime endDate;
        try {
            // Assuming ISO-8601 format, e.g. "2024-12-28T00:00:00"
            startDate = LocalDateTime.parse(start);
            endDate = LocalDateTime.parse(end);
        } catch (Exception e) {
            // If parsing fails, return Bad Request
            return ResponseEntity.badRequest().body(null);
        }

        // 2. Retrieve all customers
        List<Customer> customers = customerServices.findAllCustomers();

        // 3. Prepare a list to hold filtered purchased items
        List<Map<String, Object>> filteredPurchases = new ArrayList<>();

        // 4. Loop through each customer -> order -> productQuantity
        for (Customer customer : customers) {
            List<Order> orders = customer.getOrders();
            if (orders != null) {
                for (Order order : orders) {
                    String orderType = order.getOrderType();
                    if (orderType == null) {
                        continue; // skip if null
                    }

                    // A) If orderType == "customer_order", include ALL products
                    if (orderType.equalsIgnoreCase("customer_order")) {
                        for (Order.ProductQuantity pq : order.getProducts()) {
                            LocalDateTime purchasedAt = pq.getPurchasedAt();
                            // Check if purchasedAt is within [startDate, endDate]
                            if (purchasedAt != null
                                    && !purchasedAt.isBefore(startDate)
                                    && !purchasedAt.isAfter(endDate)) {

                                // Build the map
                                Map<String, Object> itemData = new HashMap<>();
                                itemData.put("productId", pq.getProductId());
                                itemData.put("quantity", pq.getQuantity());
                                itemData.put("purchasePrice", pq.getPurchasePrice());
                                itemData.put("purchasedAt", purchasedAt);

                                filteredPurchases.add(itemData);
                            }
                        }
                    }
                    // B) If orderType == "return", only add products with status == "On Process"
                    else if (orderType.equalsIgnoreCase("return")) {
                        for (Order.ProductQuantity pq : order.getProducts()) {
                            if ("On Process".equalsIgnoreCase(pq.getStatus())) {
                                LocalDateTime purchasedAt = pq.getPurchasedAt();
                                if (purchasedAt != null
                                        && !purchasedAt.isBefore(startDate)
                                        && !purchasedAt.isAfter(endDate)) {

                                    Map<String, Object> itemData = new HashMap<>();
                                    itemData.put("productId", pq.getProductId());
                                    itemData.put("quantity", pq.getQuantity());
                                    itemData.put("purchasePrice", pq.getPurchasePrice());
                                    itemData.put("purchasedAt", purchasedAt);

                                    filteredPurchases.add(itemData);
                                }
                            }
                        }
                    }
                    // (Ignore other order types)
                }
            }
        }

        // 5. Return the filtered results
        return ResponseEntity.ok(filteredPurchases);
    }
    //@PreAuthorize("hasRole('salesManager')")
    @GetMapping("/minus-orders-time")
    public ResponseEntity<List<Map<String, Object>>> getMinusOrdersGivenTime(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        // 1. Parse the date/time parameters
        LocalDateTime startDate;
        LocalDateTime endDate;
        try {
            // Example: "2024-01-01T00:00:00"
            startDate = LocalDateTime.parse(start);
            endDate = LocalDateTime.parse(end);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

        // 2. Retrieve all customers
        List<Customer> customers = customerServices.findAllCustomers();

        // 3. Prepare a list for the filtered product items
        List<Map<String, Object>> filteredItems = new ArrayList<>();

        // 4. Loop through each customer -> orders -> productQuantity
        for (Customer customer : customers) {
            List<Order> orders = customer.getOrders();
            if (orders != null) {
                for (Order order : orders) {
                    String orderType = order.getOrderType();
                    if (orderType == null) {
                        continue; // skip if no orderType
                    }

                    // CASE A: orderType == "return" && product status == "Refund Accepted"
                    if (orderType.equalsIgnoreCase("return")) {
                        for (Order.ProductQuantity pq : order.getProducts()) {
                            if ("Refund Accepted".equalsIgnoreCase(pq.getStatus())) {
                                LocalDateTime purchasedAt = pq.getPurchasedAt();
                                // Check if purchasedAt is in [startDate, endDate]
                                if (purchasedAt != null
                                        && !purchasedAt.isBefore(startDate)
                                        && !purchasedAt.isAfter(endDate)) {

                                    Map<String, Object> itemData = new HashMap<>();
                                    itemData.put("productId", pq.getProductId());
                                    itemData.put("quantity", pq.getQuantity());
                                    itemData.put("purchasePrice", pq.getPurchasePrice());
                                    itemData.put("purchasedAt", purchasedAt);

                                    filteredItems.add(itemData);
                                }
                            }
                        }
                    }
                    // CASE B: orderType == "canceled" -> include ALL products
                    else if (orderType.equalsIgnoreCase("canceled")) {
                        for (Order.ProductQuantity pq : order.getProducts()) {
                            LocalDateTime purchasedAt = pq.getPurchasedAt();
                            if (purchasedAt != null
                                    && !purchasedAt.isBefore(startDate)
                                    && !purchasedAt.isAfter(endDate)) {

                                Map<String, Object> itemData = new HashMap<>();
                                itemData.put("productId", pq.getProductId());
                                itemData.put("quantity", pq.getQuantity());
                                itemData.put("purchasePrice", pq.getPurchasePrice());
                                itemData.put("purchasedAt", purchasedAt);

                                filteredItems.add(itemData);
                            }
                        }
                    }
                    // Ignore other order types
                }
            }
        }

        // 5. Return the filtered results
        return ResponseEntity.ok(filteredItems);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) {
        // Step 1: Fetch all customers
        List<Customer> customers = customerServices.findAllCustomers();

        // Step 2: Search for the order within each customer
        for (Customer customer : customers) {
            List<Order> orders = customer.getOrders();
            if (orders != null) {
                for (Order order : orders) {
                    if (order.getOrderId().equals(orderId)) {
                        // Step 3: Remove the order
                        orders.remove(order);
                        customerServices.saveCustomer(customer); // Save updated customer
                        return ResponseEntity.ok("Order with ID " + orderId + " has been deleted successfully.");
                    }
                }
            }
        }

        // Step 4: If no matching order is found
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Order with ID " + orderId + " not found.");
    }

}
