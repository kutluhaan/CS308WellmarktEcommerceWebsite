package wellmarkt_cs308.wellmarkt_cs308_ecommerce.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller.SalesManagerController;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.LoginRequest;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.SalesManager;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the SalesManagerController.
 */
@WebMvcTest(SalesManagerController.class)
@AutoConfigureMockMvc(addFilters = false) // disable Spring Security filters
public class SalesManagerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalesManagerServices salesManServ;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PdfServices pdfServices;

    @MockBean
    private InvoicesServices invoicesServices;

    // Additional services needed by the SalesManagerController constructor
    @MockBean
    private CustomerServices custServ;

    @MockBean
    private ProductManagerServices prodManServ;

    @MockBean
    private AdminServices adminServ;

    @MockBean
    private AuthServices authServ;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateSalesManager_Success() throws Exception {
        // Given
        SalesManager requestManager = new SalesManager(
                null, "John", null, "Doe", "pass123", "john.doe@example.com"
        );
        SalesManager savedManager = new SalesManager(
                "SM123", "John", null, "Doe", "pass123", "john.doe@example.com"
        );

        // The controller checks if the email already exists in SalesManager, etc.
        // Let’s assume it does not exist:
        when(custServ.findByEmail("john.doe@example.com")).thenReturn(null);
        when(salesManServ.findByEmail("john.doe@example.com")).thenReturn(null);
        when(prodManServ.findByEmail("john.doe@example.com")).thenReturn(null);
        when(adminServ.findByEmail("john.doe@example.com")).thenReturn(null);

        // Then it calls salesManServ.createSalesManager() to save
        when(salesManServ.createSalesManager(any(SalesManager.class))).thenReturn(savedManager);

        // When
        mockMvc.perform(post("/api/sales-manager/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestManager)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("SalesManager created successfully"))
                .andExpect(jsonPath("$.smID").value("SM123"));
    }

    @Test
    public void testCreateSalesManager_EmailExists() throws Exception {
        // Given
        SalesManager requestManager = new SalesManager(
                null, "John", null, "Doe", "pass123", "existing@example.com"
        );
        // Suppose one already exists in DB:
        SalesManager existingManager = new SalesManager(
                "SM999", "John", null, "Doe", "pass123", "existing@example.com"
        );

        when(salesManServ.findByEmail("existing@example.com")).thenReturn(existingManager);

        // When
        mockMvc.perform(post("/api/sales-manager/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestManager)))
                // Then
                .andExpect(status().isConflict())  // 409
                .andExpect(jsonPath("$.message").value("there exists a user with this email"));
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password123", null);
        SalesManager manager = new SalesManager("SM123", "John", null, "Doe", "password123", "john.doe@example.com");

        when(salesManServ.findByEmail("john.doe@example.com")).thenReturn(manager);
        when(jwtService.generateToken(manager)).thenReturn("valid-token");

        // When
        mockMvc.perform(post("/api/sales-manager/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Authorization").value("Bearer valid-token"))
                .andExpect(jsonPath("$.message").value("succesfull login with token attached"));
    }

    @Test
    public void testLogin_InvalidCredentials() throws Exception {
        // user not found
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "wrongpass", null);

        when(salesManServ.findByEmail("nonexistent@example.com")).thenReturn(null);

        mockMvc.perform(post("/api/sales-manager/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("invalid e-mail or password"));
    }

    @Test
    public void testSetProductPrice_Success() throws Exception {
        // Token: "Bearer valid-token"
        when(jwtService.getIDFromToken("valid-token")).thenReturn("SM-111");
        when(salesManServ.setProductPriceById("SM-111", "PROD123", 99.99)).thenReturn("success");

        mockMvc.perform(put("/api/sales-manager/set-price")
                        .header("Authorization", "Bearer valid-token")
                        .param("productId", "PROD123")
                        .param("newPrice", "99.99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Price updated successfully"))
                .andExpect(jsonPath("$.productId").value("PROD123"))
                .andExpect(jsonPath("$.newPrice").value(99.99));
    }

    @Test
    public void testSetProductPrice_Unauthorized() throws Exception {
        // No Authorization header
        mockMvc.perform(put("/api/sales-manager/set-price")
                        .param("productId", "PROD123")
                        .param("newPrice", "99.99"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("unauthorized access"));
    }

    @Test
    public void testSetDiscount_Success() throws Exception {
        when(jwtService.getIDFromToken("valid-token")).thenReturn("SM-111");
        when(salesManServ.setDiscountOnProduct("SM-111", "PROD123", 20.0))
                .thenReturn("success");

        mockMvc.perform(put("/api/sales-manager/set-discount")
                        .header("Authorization", "Bearer valid-token")
                        .param("productId", "PROD123")
                        .param("discount", "20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Discount applied successfully"))
                .andExpect(jsonPath("$.discountPercent").value(20.0));
    }

    @Test
    public void testGetInvoiceAsPDF_Success() throws Exception {
        // Suppose invoice "INV123" exists
        Invoice invoice = new Invoice(
                "INV123",
                "customer@example.com",
                "123 Main St",
                LocalDate.now(),
                "John Doe",
                "**** **** **** 1234",
                "J*** D***",
                200.0
        );

        byte[] pdfBytes = "Pretend PDF content".getBytes();
        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

        when(invoicesServices.findInvoiceById("INV123")).thenReturn(invoice);
        when(pdfServices.createInvoicePdfAsByteArray(invoice)).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/sales-manager/invoices/pdf/INV123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Invoice retrieved successfully"))
                .andExpect(jsonPath("$.invId").value("INV123"))
                .andExpect(jsonPath("$.pdfBase64").value(base64Pdf));
    }

    @Test
    public void testGetInvoiceAsPDF_NotFound() throws Exception {
        // Suppose invoice "INV999" does not exist
        when(invoicesServices.findInvoiceById("INV999")).thenReturn(null);

        mockMvc.perform(get("/api/sales-manager/invoices/pdf/INV999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("there is no such invoice in database"));
    }

    @Test
    public void testGetAllInvoices_Success() throws Exception {
        // Example invoice data
        Invoice invoice1 = new Invoice("INV001",
                "cust1@example.com",
                "Main St",
                LocalDate.now(),
                "John Doe",
                "****",
                "JD",
                100.0
        );
        Invoice invoice2 = new Invoice("INV002",
                "cust2@example.com",
                "Second Ave",
                LocalDate.now(),
                "Jane Doe",
                "****",
                "JD",
                200.0
        );
        byte[] fakePdf = "Fake PDF".getBytes();

        when(invoicesServices.allInvoices()).thenReturn(Arrays.asList(invoice1, invoice2));
        // each invoice might be decoded -> re-encrypted, etc.
        // just mock the pdf
        when(pdfServices.createInvoicePdfAsByteArray(any(Invoice.class))).thenReturn(fakePdf);

        // Suppose decrypt on shipping address, cardNumber, etc. won't error
        Mockito.doReturn("decryptedHolderName").when(authServ).decrypt(anyString());

        mockMvc.perform(get("/api/sales-manager/get-all-invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All invoices retrieved successfully"))
                .andExpect(jsonPath("$.invoices").isArray());
    }

    @Test
    public void testGetAllInvoices_NoInvoices() throws Exception {
        when(invoicesServices.allInvoices()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/sales-manager/get-all-invoices"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No invoices found in the database"));
    }
}
