package wellmarkt_cs308.wellmarkt_cs308_ecommerce.ControllerTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller.DeliveryController;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Delivery;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.DeliveryServices;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(DeliveryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DeliveryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryServices deliveryServices;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAllDeliveries_Success() throws Exception {
        List<Delivery> deliveries = Arrays.asList(
                new Delivery("1", "customer1", List.of("product1"), 1, 100.0, "Address 1", "Pending"),
                new Delivery("2", "customer2", List.of("product2"), 2, 200.0, "Address 2", "Delivered")
        );

        when(deliveryServices.allDeliveries()).thenReturn(deliveries);

        mockMvc.perform(get("/api/delivery/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("deliveries retrieved successfully"))
                // Verify that the first delivery has deliveryAddress = "Address 1"
                .andExpect(jsonPath("$.deliveries[0].deliveryAddress").value("Address 1"))
                // Verify that the second delivery has deliveryAddress = "Address 2"
                .andExpect(jsonPath("$.deliveries[1].deliveryAddress").value("Address 2"));
    }

    @Test
    public void testSaveDelivery_Success() throws Exception {
        // We'll mock the returned/saved Delivery so it's not null
        Delivery requestDelivery = new Delivery(
                null, // We'll let the "save" generate or set it
                "customer1",
                List.of("product1"),
                1,
                100.0,
                "Address 1",
                "Pending"
        );

        // The "saved" object (simulating what the DB might return)
        Delivery savedDelivery = new Delivery(
                "1",
                "customer1",
                List.of("product1"),
                1,
                100.0,
                "Address 1",
                "Pending"
        );

        when(deliveryServices.saveDelivery(any(Delivery.class))).thenReturn(savedDelivery);

        mockMvc.perform(post("/api/delivery/add-delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDelivery)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("delivery saved successfully"))
                .andExpect(jsonPath("$.dID").value("1"));
    }

    @Test
    public void testGetDeliveryById_Success() throws Exception {
        Delivery delivery = new Delivery(
                "1",
                "customer1",
                List.of("product1"),
                1,
                100.0,
                "Address 1",
                "Pending"
        );

        when(deliveryServices.findDeliveryById("1")).thenReturn(delivery);

        mockMvc.perform(get("/api/delivery/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("deliveries retrieved successfully"))
                // Check the "deliveryAddress" field instead of "address"
                .andExpect(jsonPath("$.deliveries.deliveryAddress").value("Address 1"));
    }

    @Test
    public void testDeleteDeliveryById_Success() throws Exception {
        when(deliveryServices.deleteDelivery("1")).thenReturn(true);

        mockMvc.perform(delete("/api/delivery/delete-delivery/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("delivery successfully deleted"));
    }

    @Test
    public void testDeleteDeliveryById_Failure() throws Exception {
        when(deliveryServices.deleteDelivery("1")).thenReturn(false);

        mockMvc.perform(delete("/api/delivery/delete-delivery/1"))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.response").value("failed to delete"));
    }
}
