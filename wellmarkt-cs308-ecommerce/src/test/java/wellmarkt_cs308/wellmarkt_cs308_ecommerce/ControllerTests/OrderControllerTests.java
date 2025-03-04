package wellmarkt_cs308.wellmarkt_cs308_ecommerce.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller.OrderController;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Customer;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Order;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Order.ProductQuantity;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.AuthServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CustomerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.EmailServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.OrderServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductServices;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Example test class that addresses:
 * - NullPointerExceptions by mocking product lookups
 * - Empty ProductQuantity list by adding a sample product
 * - Searching for orders by mocking customerServices.findAllCustomers()
 */
@WebMvcTest(OrderController.class)
class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderServices orderServices;

    @MockBean
    private CustomerServices customerServices;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ProductServices productServices;

    @MockBean
    private EmailServices emailServices;

    @MockBean
    private AuthServices authServices;

    @MockBean
    private SecurityFilterChain securityFilterChain; // Bypass security

    @Autowired
    private ObjectMapper objectMapper;

    private Customer createTestCustomer() throws Exception {
        Customer customer = new Customer(
                "customer1",
                "John",
                "M",
                "Doe",
                "john.doe@example.com",
                "password",
                "123 Main St",
                "USA",
                "1234567890",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,       // wishlist
                new ArrayList<>(), // cart
                new ArrayList<>()  // orders
        );
        return customer;
    }


    private Order createTestOrder() {
        // Create sample ProductQuantity
        ProductQuantity pq = new ProductQuantity("product1", 2);
        pq.setStatus("On Process");
        pq.setPurchasePrice(99.99); // example
        pq.setPurchasedAt(LocalDateTime.now());

        // Attach to an Order
        List<ProductQuantity> productList = new ArrayList<>();
        productList.add(pq);

        Order order = new Order("customer1", productList);
        order.setOrderId("order1");
        order.setOrderType("customer_order");
        order.setAddress("Sample Address");
        return order;
    }

    /**
     * GET /api/orders/all
     */
    @Test
    @WithMockUser(username = "customer1", roles = {"USER"})
    void testGetAllOrders_Success() throws Exception {
        // 1) Create a customer with 1 order
        Customer customer = createTestCustomer();
        Order order = createTestOrder();
        customer.setOrders(List.of(order));

        // 2) Mock findAllCustomers() to return that single customer
        when(customerServices.findAllCustomers()).thenReturn(List.of(customer));

        // 3) Perform GET request
        mockMvc.perform(get("/api/orders/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("order1"))
                .andExpect(jsonPath("$[0].orderType").value("customer_order"));
    }

    /**
     * PUT /api/orders/update-status/{orderId}
     */
    @Test
    @WithMockUser(username = "customer1", roles = {"USER"})
    void testUpdateOrderStatus_Success() throws Exception {
        // 1) Create a test customer that has order "order1" -> has product "product1"
        Customer customer = createTestCustomer();
        Order order = createTestOrder();
        customer.setOrders(List.of(order));

        // 2) Because the actual code does:
        //    List<Customer> customers = customerServices.findAllCustomers();
        //    ... it loops to find orderId "order1"
        when(customerServices.findAllCustomers()).thenReturn(List.of(customer));

        // There's no call to orderServices.findOrderById in the real code path,
        // but let's just ensure it doesn't blow up if it does get called
        when(orderServices.findOrderById("order1")).thenReturn(java.util.Optional.of(order));

        // 3) Perform the PUT: we want to set the product1 status to "Delivered"
        mockMvc.perform(put("/api/orders/update-status/{orderId}", "order1")
                        .param("productId", "product1")
                        .param("newStatus", "Delivered")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("customer_order siparişinin durumu güncellendi: Delivered"));
    }

    /**
     * PUT /api/orders/cancel-order/{orderId}
     */
    @Test
    @WithMockUser(username = "customer1", roles = {"USER"})
    void testCancelWholeOrder_Success() throws Exception {
        // 1) Create a test customer with 1 order that has product "product1"
        Customer customer = createTestCustomer();
        Order order = createTestOrder();  // "On Process" => so it can be canceled
        customer.setOrders(List.of(order));

        // 2) Mock findAllCustomers() so the code can find the order
        when(customerServices.findAllCustomers()).thenReturn(List.of(customer));

        // 3) We also need a mock product so that .findProductById("product1") doesn't return null
        Product mockProduct = new Product();
        mockProduct.setpID("product1");
        mockProduct.setName("Sample Product");
        mockProduct.setStock(100);
        when(productServices.findProductById("product1")).thenReturn(mockProduct);

        // 4) Do the PUT request
        mockMvc.perform(
                        put("/api/orders/cancel-order/{orderId}", "order1")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Sipariş başarıyla iptal edildi. Toplam iade: 199.98₺"));
    }

    /**
     * GET /api/orders/my-orders
     */
    @Test
    @WithMockUser(username = "customer1", roles = {"USER"})
    void testGetUserOrders_Success() throws Exception {
        // 1) Create a test customer with 1 order that has product "product1"
        Customer customer = createTestCustomer();
        Order order = createTestOrder();
        customer.setOrders(List.of(order));

        // 2) The code in getUserOrders() calls:
        //    -> jwtService.getIDFromToken("valid-token") -> "customer1"
        //    -> customerServices.findById("customer1") -> returns our test customer
        when(jwtService.getIDFromToken("valid-token")).thenReturn("customer1");
        when(customerServices.findById("customer1")).thenReturn(customer);

        // Also need to mock product retrieval so we don’t get NPE in getName() or getImageURL()
        Product mockProduct = new Product();
        mockProduct.setpID("product1");
        mockProduct.setName("Test Product");
        mockProduct.setImageURL("http://test-image.jpg");
        mockProduct.setStock(5);
        when(productServices.findProductById("product1")).thenReturn(mockProduct);

        // 3) Perform GET request with the “valid-token”
        mockMvc.perform(get("/api/orders/my-orders")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("order1"))
                .andExpect(jsonPath("$[0].products[0].product_name").value("Test Product"));
    }
}
