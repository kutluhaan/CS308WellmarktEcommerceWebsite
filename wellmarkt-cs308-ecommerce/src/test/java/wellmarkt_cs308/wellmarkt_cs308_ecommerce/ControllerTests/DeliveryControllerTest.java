package wellmarkt_cs308.wellmarkt_cs308_ecommerce.ControllerTests;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import java.util.Collections;
import java.util.List;

@WebMvcTest(DeliveryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryServices deliveryServices;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testDeleteDeliveryById_Success() throws Exception { // Test for successfully deleting a delivery by its ID
        when(deliveryServices.deleteDelivery("1")).thenReturn(true);

        mockMvc.perform(delete("/api/delivery/delete-delivery/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("delivery successfully deleted"));
    }

    @Test
    public void testDeleteDeliveryById_Failure() throws Exception { // Test for failure to delete a delivery by its ID
        when(deliveryServices.deleteDelivery("1")).thenReturn(false);

        mockMvc.perform(delete("/api/delivery/delete-delivery/1"))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.response").value("failed to delete"));
    }
}
