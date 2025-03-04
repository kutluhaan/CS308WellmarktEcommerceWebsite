package wellmarkt_cs308.wellmarkt_cs308_ecommerce.ControllerTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.ResultActions;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller.ProductController;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Category;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CategoryServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductServices;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for these tests
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServices productServices;

    @MockBean
    private CategoryServices categoryServices;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetProductById_Success() throws Exception {
        String productId = "product123";
        Category cat = new Category();
        cat.setCatID("cat1");
        cat.setCatName("Electronics");

        Product product = new Product(
                productId,
                "Test Product",
                "A test product description",
                "http://example.com/img.jpg",
                5,
                true,
                "Model X",
                "SNX123",
                999.99,
                true,
                "Distributor Info",
                4.8,
                cat,
                "TestBrand",
                LocalDateTime.now(),
                LocalDateTime.now(),
                10
        );

        when(productServices.findProductById(productId)).thenReturn(product);

        ResultActions result = mockMvc.perform(
                get("/api/products/{pID}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.pID").value(productId))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.model").value("Model X"))
                .andExpect(jsonPath("$.brand").value("TestBrand"))
                .andExpect(jsonPath("$.category.catID").value("cat1"))
                .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    public void testGetAllProducts_Success() throws Exception {
        Category cat1 = new Category();
        cat1.setCatID("cat1");
        cat1.setCatName("Category1");

        Category cat2 = new Category();
        cat2.setCatID("cat2");
        cat2.setCatName("Category2");

        Product product1 = new Product(
                "product1",
                "Product1",
                "Desc1",
                "http://example.com/img1.jpg",
                10,
                true,
                "Model1",
                "SN111",
                100.0,
                true,
                "Distributor1",
                4.0,
                cat1,
                "Brand1",
                LocalDateTime.now(),
                LocalDateTime.now(),
                10
        );

        Product product2 = new Product(
                "product2",
                "Product2",
                "Desc2",
                "http://example.com/img2.jpg",
                20,
                true,
                "Model2",
                "SN222",
                200.0,
                true,
                "Distributor2",
                4.5,
                cat2,
                "Brand2",
                LocalDateTime.now(),
                LocalDateTime.now(),
                20
        );

        List<Product> productList = Arrays.asList(product1, product2);

        when(productServices.getAllProducts()).thenReturn(productList);

        ResultActions result = mockMvc.perform(get("/api/products/all-products"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].pID").value("product1"))
                .andExpect(jsonPath("$.products[1].pID").value("product2"))
                .andExpect(jsonPath("$.response").value("products retrieved successfully"));
    }
    @Test
    public void testAddProduct_Success() throws Exception {
        // Prepare a category
        Category cat = new Category();
        cat.setCatID("cat1");
        cat.setCatName("CategoryName1");

        // Prepare a product
        Product newProduct = new Product(
                "productABC",
                "New Product",
                "New product desc",
                "http://example.com/img.jpg",
                5,
                true,
                "ModelABC",
                "SN-ABC",
                49.99,
                true,
                "DistInfo",
                3.5,
                cat,
                "ABCBrand",
                LocalDateTime.now(),
                LocalDateTime.now(),
                5
        );

        when(productServices.saveProduct(any(Product.class))).thenReturn(newProduct);

        // Perform
        mockMvc.perform(
                        post("/api/products/add-product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newProduct))
                )
                .andExpect(status().isCreated())
                // Assert the actual response matches the expected structure
                .andExpect(jsonPath("$.product").value("productABC"))
                .andExpect(jsonPath("$.response").value("product created successfully"));
    }

}
