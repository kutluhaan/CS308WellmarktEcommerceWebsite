package wellmarkt_cs308.wellmarkt_cs308_ecommerce.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller.WishlistController;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Wishlist;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.WishlistServices;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WishlistControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistServices wishlistService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddProductToWishlist_Success() throws Exception { // Test for successfully adding a product to a customer's wishlist
        // Prepare test data
        String token = "valid-token";
        String customerId = "12345";
        String productId = "prod1";

        when(jwtService.getIDFromToken(token)).thenReturn(customerId);
        doNothing().when(wishlistService).addProductToWishlist(customerId, productId);

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/wishlist/add")
                .header("Authorization", "Bearer " + token)
                .param("productId", productId));

        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.cID").value(customerId))
              .andExpect(jsonPath("$.message").value("Product added to wishlist"));
    }

    @Test
    public void testAddProductToWishlist_InvalidToken() throws Exception { // Test for failure when an invalid token is provided
        // Prepare test data
        String token = "invalid-token";
        
        when(jwtService.getIDFromToken(anyString())).thenReturn(null);

        // Perform the request
        ResultActions result = mockMvc.perform(post("/api/wishlist/add")
                .header("Authorization", "Bearer " + token)
                .param("productId", "prod1"));

        // Verify the response
        result.andExpect(status().isUnauthorized())
              .andExpect(jsonPath("$.message").value("invalid token"));
    }

    @Test
    public void testRemoveProductFromWishlist_Success() throws Exception { // Test for successfully removing a product from a customer's wishlist
        // Prepare test data
        String token = "valid-token";
        String customerId = "12345";
        String productId = "prod1";

        when(jwtService.getIDFromToken(token)).thenReturn(customerId);
        doNothing().when(wishlistService).removeProductFromWishlist(customerId, productId);

        // Perform the request
        ResultActions result = mockMvc.perform(delete("/api/wishlist/remove")
                .header("Authorization", "Bearer " + token)
                .param("productId", productId));

        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.cID").value(customerId))
              .andExpect(jsonPath("$.message").value("Product removed from the wishlist"));
    }

    @Test
    public void testRemoveProductFromWishlist_InvalidToken() throws Exception { // Test for failure when trying to remove a product with an invalid token
        // Prepare test data
        String token = "invalid-token";
        
        when(jwtService.getIDFromToken(anyString())).thenReturn(null);

        // Perform the request
        ResultActions result = mockMvc.perform(delete("/api/wishlist/remove")
                .header("Authorization", "Bearer " + token)
                .param("productId", "prod1"));

        // Verify the response
        result.andExpect(status().isUnauthorized())
              .andExpect(jsonPath("$.message").value("invalid token"));
    }

    @Test
    public void testGetWishlistByCustomerId_Success() throws Exception { // Test for successfully retrieving the wishlist of a specific customer
        
        // Prepare test data
        String token = "valid-token";
        String customerId = "12345";
        Product product = new Product("prod1", "Product 1", "Description", "imageURL", 10, true, "Model1", "SN123", 100.0, true, "Distributor1", 4.5, null, "Brand1", LocalDateTime.now(), LocalDateTime.now(), 10);
        Wishlist wishlist = new Wishlist(customerId, Collections.singletonList(product));

        when(jwtService.getIDFromToken(token)).thenReturn(customerId);
        when(wishlistService.getWishlistByCustomerId(customerId)).thenReturn(wishlist);

        // Perform the request
        ResultActions result = mockMvc.perform(get("/api/wishlist/get-user-wishlist")
                .header("Authorization", "Bearer " + token));

        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.cID").value(customerId))
              .andExpect(jsonPath("$.wishlist").exists())
              .andExpect(jsonPath("$.message").value("Wishlist retrieved successfully"));
    }

    @Test
    public void testGetWishlistByCustomerId_NotFound() throws Exception { // Test for handling the case where a customer's wishlist is not found
        // Prepare test data
        String token = "valid-token";
        String customerId = "12345";

        when(jwtService.getIDFromToken(token)).thenReturn(customerId);
        when(wishlistService.getWishlistByCustomerId(customerId)).thenReturn(null);

        // Perform the request
        ResultActions result = mockMvc.perform(get("/api/wishlist/get-user-wishlist")
                .header("Authorization", "Bearer " + token));

        // Verify the response
        result.andExpect(status().isNotFound())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.cID").value(customerId))
              .andExpect(jsonPath("$.message").value("requested wishlist is not found"));
    }

    @Test
    public void testGetCustomersWithProduct_Success() throws Exception { // Test for retrieving all customers who have a specific product in their wishlist
        // Prepare test data
        String productId = "prod1";
        Product product = new Product("prod1", "Product 1", "Description", "imageURL", 10, true, "Model1", "SN123", 100.0, true, "Distributor1", 4.5, null, "Brand1", LocalDateTime.now(), LocalDateTime.now(), 10);
        Wishlist wishlist1 = new Wishlist("12345", Collections.singletonList(product));
        Wishlist wishlist2 = new Wishlist("67890", Collections.singletonList(product));

        when(wishlistService.getCustomersWithProduct(productId)).thenReturn(Arrays.asList(wishlist1, wishlist2));

        // Perform the request
        ResultActions result = mockMvc.perform(get("/api/wishlist/product/{productId}", productId));

        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.pID").value(productId))
              .andExpect(jsonPath("$.customers[0].customerId").value("12345"))
              .andExpect(jsonPath("$.customers[1].customerId").value("67890"));
    }
}