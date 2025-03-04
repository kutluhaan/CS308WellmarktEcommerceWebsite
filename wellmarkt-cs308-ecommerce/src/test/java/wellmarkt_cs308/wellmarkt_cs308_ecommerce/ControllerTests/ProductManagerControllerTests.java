package wellmarkt_cs308.wellmarkt_cs308_ecommerce.ControllerTests; 

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller.ProductManagerController;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.*;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.*;

@WebMvcTest(ProductManagerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductManagerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductManagerServices prodManServ;

    @MockBean
    private ProductServices prodServ;

    @MockBean
    private CommentServices comServ;

    @MockBean
    private InvoicesServices invServ;

    @MockBean
    private DeliveryServices delServ;

    @MockBean
    private CategoryServices catServ;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthServices auth;

    @MockBean
    private CustomerServices custServ;

    @MockBean
    private SalesManagerServices salesManServ;

    @MockBean
    private AdminServices adminServ;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testCreateProductManagerAccount_AlreadyExists() throws Exception {
        // 1. Prepare a ProductManager object to send in the request body
        ProductManager productManager = new ProductManager(
                "pm1",                          // pmID
                "John",                         // firstName
                null,                           // middleName
                "Doe",                          // lastName
                "password123",                  // password
                "john.doe@example.com",         // email
                null                            // phone
        );

        // 2. Mock the scenario in which the email is already used by a ProductManager
        //    Return 'productManager' (non-null) from prodManServ.findByEmail(...)
        //    Ensure the other services return null, so the code sees that one is not null → conflict
        when(custServ.findByEmail("john.doe@example.com")).thenReturn(null);
        when(salesManServ.findByEmail("john.doe@example.com")).thenReturn(null);
        when(prodManServ.findByEmail("john.doe@example.com")).thenReturn(productManager);
        when(adminServ.findByEmail("john.doe@example.com")).thenReturn(null);

        // 3. Perform the POST request
        mockMvc.perform(post("/api/product-manager/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productManager)))
                // 4. Assert the status and response
                .andExpect(status().isConflict())  // 409
                .andExpect(jsonPath("$.message").value("there exists a user with this email"))
                .andExpect(jsonPath("$.pmID").value("pm1"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }


    @Test
    public void testAllProducts_Success() throws Exception { // Test for successfully retrieving all products
        // Create test category
        Category category1 = new Category();
        category1.setCatID("cat1");
        category1.setCatName("Category1");

        Category category2 = new Category();
        category2.setCatID("cat2");
        category2.setCatName("Category2");

        // Create test products
        Product product1 = new Product("p1", "Product1", "Description1", "ImageURL1", 10, true, "Model1", 
                                        "SN123", 99.99, true, "Distributor1", 4.5, 
                                        category1, "Brand1", 
                                        LocalDateTime.now(), LocalDateTime.now(), 10);

        Product product2 = new Product("p2", "Product2", "Description2", "ImageURL2", 20, true, "Model2", 
                                        "SN456", 199.99, true, "Distributor2", 4.7, 
                                        category2, "Brand2", 
                                        LocalDateTime.now(), LocalDateTime.now(), 20);

        List<Product> productList = List.of(product1, product2);

        // Mock service behavior
        when(prodServ.allProducts()).thenReturn(productList);

        // Perform request and assert results
        mockMvc.perform(get("/api/product-manager/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].pID").value("p1"))
                .andExpect(jsonPath("$.products[0].name").value("Product1"))
                .andExpect(jsonPath("$.products[1].pID").value("p2"))
                .andExpect(jsonPath("$.products[1].name").value("Product2"));
    }


    @Test
    public void testAllProducts_NoProductsFound() throws Exception { // Test for handling the case where no products are found
        when(prodServ.allProducts()).thenReturn(null); // Change to null instead of empty list

        mockMvc.perform(get("/api/product-manager/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("There is no product to retrieve."))
                .andExpect(jsonPath("$.products").value("none"));
    }


    @Test
    public void testAddProduct_InvalidCategory() throws Exception { // Test for adding a product with an invalid category
        Category category = new Category();
        category.setCatID("invalidCat");
        category.setCatName("InvalidCategory");

        Product product = new Product("p1", "Product1", "Description1", "ImageURL1", 10, true, "Model1", 
                                    "SN123", 99.99, true, "Distributor1", 4.5, 
                                    category, "Brand1", 
                                    LocalDateTime.now(), LocalDateTime.now(), 10);

        // Mock category service to return null for invalid category
        when(catServ.findById("invalidCat")).thenReturn(null);

        mockMvc.perform(post("/api/product-manager/products/add-product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.response").value("product has an invalid category"));
    }
    
    @Test
    public void testAddProduct_AlreadyExists() throws Exception { // Test for handling the case where a product already exists
        Category category = new Category();
        category.setCatID("cat1");
        category.setCatName("Category1");

        // Mock the category service to return the category
        when(catServ.findById("cat1")).thenReturn(category);

        Product product = new Product("p1", "Product1", "Description1", "ImageURL1", 10, true, "Model1", 
                                    "SN123", 99.99, true, "Distributor1", 4.5, 
                                    category, "Brand1", 
                                    LocalDateTime.now(), LocalDateTime.now(), 10);

        when(prodServ.isProductExists("p1")).thenReturn(true);

        mockMvc.perform(post("/api/product-manager/products/add-product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.response").value("product already exists"));
    }


    @Test
    public void testDeleteProductById_Success() throws Exception { // Test for successfully deleting a product by its ID
        // Mock service behavior
        when(prodServ.isProductExists("p1")).thenReturn(true).thenReturn(false);

        // Perform request and assert results
        mockMvc.perform(delete("/api/product-manager/products/delete-product/p1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response").value("successfully deleted"));
    }

    @Test
    public void testDeleteProductById_NotFound() throws Exception { // Test for handling the case where a product to be deleted is not found
        // Mock service behavior
        when(prodServ.isProductExists("p1")).thenReturn(false);

        // Perform request and assert results
        mockMvc.perform(delete("/api/product-manager/products/delete-product/p1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response").value("product does not exists"));
    }
} 