package wellmarkt_cs308.wellmarkt_cs308_ecommerce.ControllerTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller.CustomerController;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.CartItem;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Customer;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.LoginRequest;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.RatingRequest;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.AdminServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.AuthServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CustomerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductManagerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.SalesManagerServices;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerServices custServ;

    @MockBean
    private AuthServices auth;

    @MockBean
    private JwtService jwtService;

    /**
     * IMPORTANT: Add the missing mock beans for all dependencies
     * that appear in the CustomerController’s constructor.
     */
    @MockBean
    private SalesManagerServices salesManServ;

    @MockBean
    private ProductManagerServices prodManServ;

    @MockBean
    private AdminServices adminServ;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddCustomer_Success() throws Exception {
        // Prepare test data
        Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPassword("password123");

        Customer savedCustomer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        savedCustomer.setcID("12345");
        savedCustomer.setFirstName("John");
        savedCustomer.setLastName("Doe");
        savedCustomer.setEmail("john.doe@example.com");
        savedCustomer.setPassword("hashedPassword");

        // Mock behaviors
        when(auth.hashPassword("password123")).thenReturn("hashedPassword");
        when(custServ.saveCustomer(any(Customer.class))).thenReturn(savedCustomer);

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/customer/add-customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)));

        // Verify the response
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.cID").value("12345"))
                .andExpect(jsonPath("$.response").value("Customer successfully created"));
    }

    @Test
    public void testAddCustomer_Failure() throws Exception {
        // Prepare test data
        Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        customer.setEmail("jane.smith@example.com");
        customer.setPassword("password456");

        // Mock behaviors
        when(auth.hashPassword("password456")).thenReturn("hashedPassword");
        when(custServ.saveCustomer(any(Customer.class))).thenThrow(new RuntimeException("Database error"));

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/customer/add-customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)));

        // Verify the response
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.response").value("Failed to create the customer"));
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Prepare test data
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password123", Optional.empty());

        Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        customer.setcID("12345");
        customer.setEmail("john.doe@example.com");
        customer.setPassword("hashedPassword");

        // Mock behaviors
        when(custServ.findByEmail("john.doe@example.com")).thenReturn(customer);
        when(auth.verifyPassword("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(customer)).thenReturn("jwt-token");

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/customer/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Verify the response
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.Authorization").value("Bearer jwt-token")); // Adjusted to only check Authorization field
    }

    @Test
    public void testLogin_InvalidEmail() throws Exception {
        // Prepare test data
        LoginRequest loginRequest = new LoginRequest("invalid@example.com", "password123", Optional.empty());

        // Mock behaviors
        when(custServ.findByEmail("invalid@example.com")).thenReturn(null);

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/customer/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Verify the response
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.response").value("Customer is not found.")); // Match the actual message
    }



    @Test
    public void testLogin_InvalidPassword() throws Exception {
        // Prepare test data
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "wrongpassword", Optional.empty());

        Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        customer.setEmail("john.doe@example.com");
        customer.setPassword("hashedPassword");

        // Mock behaviors
        when(custServ.findByEmail("john.doe@example.com")).thenReturn(customer);
        when(auth.verifyPassword("wrongpassword", "hashedPassword")).thenReturn(false);

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/customer/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Verify the response
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.response").value("Invalid e-mail or password."));
    }

    @Test
    public void testLogin_JwtCreationError() throws Exception {
        // Prepare test data
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password123", Optional.empty());

        Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        customer.setEmail("john.doe@example.com");
        customer.setPassword("hashedPassword");

        // Mock behaviors
        when(custServ.findByEmail("john.doe@example.com")).thenReturn(customer);
        when(auth.verifyPassword("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(customer)).thenReturn(null); // Simulate JWT creation error

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/customer/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Verify the response
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.response").value("Invalid token.")); // Match actual response from the controller
    }


    @Test
    public void testFindByEmail_Success() throws Exception {
        // Prepare test data
        String email = "john.doe@example.com";
        Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        customer.setEmail(email);
        customer.setcID("12345");

        // Mock behaviors
        when(custServ.findByEmail(email)).thenReturn(customer);
        when(custServ.findById("12345")).thenReturn(customer);

        // Perform the request
        ResultActions result = mockMvc.perform(get("/api/customer/{email}", email));

        // Verify the response
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.email").value(email))
                .andExpect(jsonPath("$.response").value("customer found successfully"));
    }

    @Test
    public void testFindByEmail_NotFound() throws Exception {
        // Prepare test data
        String email = "nonexistent@example.com";

        // Mock behaviors
        when(custServ.findByEmail(email)).thenReturn(null);

        // Perform the request
        ResultActions result = mockMvc.perform(get("/api/customer/{email}", email));

        // Verify the response
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.response").value("customer not found"));
    }

    @Test
    public void testFindByEmail_Exception() throws Exception {
        // Prepare test data
        String email = "error@example.com";

        // Mock behaviors
        when(custServ.findByEmail(email)).thenThrow(new RuntimeException("Database error"));

        // Perform the request
        ResultActions result = mockMvc.perform(get("/api/customer/{email}", email));

        // Verify the response
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.response", containsString("error:")));
    }

    @Test
    public void testGetCustomerProfile_Authorized() throws Exception {
        // Prepare test data
        String userId = "12345";
        Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        customer.setcID(userId);

        // Mock behaviors
        when(custServ.findById(userId)).thenReturn(customer);
        when(jwtService.getIDFromToken("valid-token")).thenReturn(userId);

        // Perform the request with token
        ResultActions result = mockMvc.perform(get("/api/customer/profile/{userid}", userId)
                .header("Authorization", "Bearer valid-token"));

        // Verify the response
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.cID").value(userId));
    }

    @Test
    public void testGetCustomerCart_Authorized() throws Exception {
        // Prepare test data
        String userId = "12345";
        Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        customer.setcID(userId);
        customer.setCart(Arrays.asList(new CartItem("product1", 2)));

        // Mock behaviors
        when(custServ.findById(userId)).thenReturn(customer);
        when(jwtService.getIDFromToken("valid-token")).thenReturn(userId);

        // Perform the request with token
        ResultActions result = mockMvc.perform(get("/api/customer/profile/cart/{userid}", userId)
                .header("Authorization", "Bearer valid-token"));

        // Verify the response
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.cart[0].productID").value("product1"))
                .andExpect(jsonPath("$.cart[0].quantity").value(2));
    }

    @Test
    public void testGetCustomerDetails_ValidToken() throws Exception {
        // Prepare test data
        String token = "valid-token";
        String userId = "12345";
        Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        customer.setcID(userId);
        customer.setEmail("john.doe@example.com");

        // Mock behaviors
        when(jwtService.getIDFromToken(token)).thenReturn(userId);
        when(custServ.findById(userId)).thenReturn(customer);

        // Perform the request
        ResultActions result = mockMvc.perform(get("/api/customer/details")
                .header("Authorization", "Bearer " + token));

        // Verify the response
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.cID").value(userId))
                .andExpect(jsonPath("$.customer.email").value("john.doe@example.com"));
    }

    @Test
    public void testGetCustomerDetails_InvalidToken() throws Exception {
        // Perform the request without token
        ResultActions result = mockMvc.perform(get("/api/customer/details"));

        // Verify the response
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetCustomerDetails_InvalidTokenFormat() throws Exception {
        // Perform the request with invalid token format
        ResultActions result = mockMvc.perform(get("/api/customer/details")
                .header("Authorization", "InvalidToken"));

        // Verify the response
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetCustomerDetailsCart_ValidToken() throws Exception {
        // Prepare test data
        String token = "valid-token";
        String userId = "12345";
        Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        customer.setcID(userId);
        customer.setCart(Arrays.asList(new CartItem("product1", 2)));

        // Mock behaviors
        when(jwtService.getIDFromToken(token)).thenReturn(userId);
        when(custServ.findById(userId)).thenReturn(customer);

        // Perform the request
        ResultActions result = mockMvc.perform(get("/api/customer/details/cart")
                .header("Authorization", "Bearer " + token));

        // Verify the response
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.cart[0].productID").value("product1"))
                .andExpect(jsonPath("$.cart[0].quantity").value(2));
    }

    @Test
    public void testGetCustomerDetailsCart_InvalidToken() throws Exception {
        // Perform the request without token
        ResultActions result = mockMvc.perform(get("/api/customer/details/cart"));

        // Verify the response
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void testGiveRating_Success() throws Exception {
        // Prepare test data
        RatingRequest request = new RatingRequest("product1", Optional.of(4.5));

        // Mock behaviors
        when(custServ.giveRating("product1", 4.5)).thenReturn(4.2);

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/customer/give-rating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Verify the response
        result.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.object").value("product1 Optional[4.5]"))
                .andExpect(jsonPath("$.message").value("rating succesfully added to product"));
    }

    @Test
    public void testGiveRating_ProductNotFound() throws Exception {
        // Prepare test data
        RatingRequest request = new RatingRequest("invalidProduct", Optional.of(4.5));

        // Mock behaviors
        when(custServ.giveRating("invalidProduct", 4.5)).thenReturn(-1.0);

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/customer/give-rating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Verify the response
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.object").value("invalidProduct Optional[4.5]"))
                .andExpect(jsonPath("$.message").value("error in giving rating"));
    }

    @Test
    public void testGiveRating_InvalidRating() throws Exception {
        // Prepare test data
        RatingRequest request = new RatingRequest("product1", Optional.of(6.0)); // rating > 5.0

        // Mock behaviors
        when(custServ.giveRating("product1", 6.0)).thenReturn(-1.0);

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/customer/give-rating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Verify the response
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.object").value("product1 Optional[6.0]"))
                .andExpect(jsonPath("$.message").value("error in giving rating"));
    }
}

