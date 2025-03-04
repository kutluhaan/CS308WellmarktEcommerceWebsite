package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Customer;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.LoginRequest;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.SalesManager;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.SalesManagerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.AdminServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.AuthServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CustomerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.InvoicesServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.PdfServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductManagerServices;

//created by deniz gürleyen

@RestController
@RequestMapping("/api/sales-manager")
public class SalesManagerController {

	@Autowired
	private JwtService jwtService;
	 
	@Autowired
	private PdfServices pdfServices;
	
	@Autowired
    private InvoicesServices invoicesServices;

	@Autowired
	private final SalesManagerServices salesManServ;
	
	@Autowired
	private final CustomerServices custServ;
	
	@Autowired
	private final ProductManagerServices prodManServ;

	@Autowired
	private final AdminServices adminServ;

	@Autowired
	private final AuthServices authServ;
	
	
	Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);
	
	@Autowired
	public SalesManagerController(SalesManagerServices salesManServ, 
								  JwtService jwtService,
								  PdfServices pdfServices,
								  InvoicesServices invoiceServices,
								  CustomerServices custServ, 
								  ProductManagerServices prodManServ,
								  AdminServices adminServ,
								  AuthServices authServ) {
		this.invoicesServices = invoiceServices;
		this.jwtService = jwtService;
		this.pdfServices = pdfServices;
		this.salesManServ = salesManServ;
		this.adminServ = adminServ;
		this.custServ = custServ;
		this.prodManServ = prodManServ;
		this.authServ = authServ;
	}	
	
	@PostMapping("/sign-up")//working
    public ResponseEntity<Map<String, Object>> createSalesManagerAccount(@RequestBody SalesManager salesMan) {

        Map<String, Object> response = new HashMap<>();

    	if ((custServ.findByEmail(salesMan.getEmail()) == null) && 
    		(salesManServ.findByEmail(salesMan.getEmail()) == null) &&
    		(prodManServ.findByEmail(salesMan.getEmail()) == null) && 
    		(adminServ.findByEmail(salesMan.getEmail()) == null)
    	) {
            // Actually create in DB
            SalesManager savedManager = salesManServ.createSalesManager(salesMan);
            response.put("message", "SalesManager created successfully");
            response.put("smID", savedManager.getSmID());
            logger.info("Response:", response.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    	} else {
    		response.put("message", "there exists a user with this email");
    		response.put("smID", salesMan.getSmID());
    		response.put("email", salesMan.getEmail());
    		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    	}
    }


	@PostMapping("/login") //working
	public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
		
	    // 1) Find manager by email
	    SalesManager manager = salesManServ.findByEmail(loginRequest.email());
	    if (manager == null || !manager.getEmail().equals(loginRequest.email()) || !manager.getPassword().equals(loginRequest.password())) {
	    	response.put("e-mail", loginRequest.email());
	    	response.put("password", loginRequest.password());
	    	response.put("message", "invalid e-mail or password");
            logger.info("Response:", response.toString());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }

	    // 3) Generate the JWT using your new method
	    String token = jwtService.generateToken(manager);  // <-- Now using generateToken(SalesManager)
	    if (token == null) {
	    	response.put("token", "null");
	    	response.put("message", "Error during JWT creation.");
            logger.info("Response:", response.toString());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }

	    // 4) (Optional) set an HTTP session attribute, if desired
	    request.getSession().setAttribute(token, manager);

	    // 5) Return the token in both body and Authorization header
	    response.put("Authorization", "Bearer " + token);
	    response.put("message", "succesfull login with token attached");
        logger.info("Response:", response.toString());
        return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	
	//@PreAuthorize("hasRole('salesManager')") // i dont know how to preauthorize for this function but it is
	@PutMapping("/set-price") // working
    public ResponseEntity<Map<String, Object>> setProductPriceById(
            @RequestParam String productId,
            @RequestParam Double newPrice,
            HttpServletRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        
        String token = request.getHeader("Authorization");
        logger.debug("Token 1: {}", token);
        if (token == null || !token.startsWith("Bearer ")) {
            response.put("error", "unauthorized access");
            response.put("message", "please be directed");
            logger.info("Response: {}", response.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        token = token.substring(7);
        logger.info("Token 2: {}", token);
        String managerId = jwtService.getIDFromToken(token);
        logger.info("ManagerId:" + managerId + ", product ID:" + productId);
        
        // Call service to update the price
        String msg = salesManServ.setProductPriceById(managerId, productId, newPrice);

        if (Objects.equals(msg, "success")) {
            response.put("response", "Price updated successfully");
            response.put("productId", productId);
            response.put("newPrice", newPrice);
            logger.info("Response:", response.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
        	response.put("smID", managerId);
            response.put("error", "failed to set price");
            logger.info("Response:", response.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }    
    }

    //@PreAuthorize("hasRole('salesManager')")  // i dont know how to preauthorize for this function but it is
    @PutMapping("/set-discount") // working
    public ResponseEntity<Map<String, Object>> setDiscountOnProduct(
            @RequestParam String productId,
            @RequestParam double discount,
            HttpServletRequest request
    ) {
        Map<String, Object> response = new HashMap<>();

        String token = request.getHeader("Authorization");
        logger.info("Token 1 for /set-discount: {}", token);
        if (token == null || !token.startsWith("Bearer ")) {
            response.put("error", "unauthorized access");
            response.put("message", "please be directed");
            logger.info("Response /set-discount fail: {}", response.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        token = token.substring(7);
        logger.info("Token 2: {}", token);
        String managerId = jwtService.getIDFromToken(token);
        logger.info("ManagerId:" + managerId + ", product ID:" + productId);

        String msg = salesManServ.setDiscountOnProduct(managerId, productId, discount);
        if (Objects.equals(msg, "success")) {
            response.put("response", "Discount applied successfully");
            response.put("discountPercent", discount);
            logger.info("Response: {}", response.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("smID", managerId);
            response.put("error", msg);
            logger.info("Response: {}", response.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


	//@PreAuthorize("hasRole('salesManager')")  // i dont know how to preauthorize for this function but it is
	@GetMapping("/invoices/pdf/{invId}") // working
    public ResponseEntity<Map<String, Object>> getInvoiceAsPDF(@PathVariable String invId) {
        Map<String, Object> response = new HashMap<>();
    	
        // 1) Find the invoice by ID
        Invoice invoice = invoicesServices.findInvoiceById(invId);
        if (invoice == null) {
        	response.put("message", "there is no such invoice in database");
        	response.put("invId", invId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            try {
                // 2) Generate PDF as bytes
                byte[] pdfBytes = pdfServices.createInvoicePdfAsByteArray(invoice);

                // 3) Return it as a file download
                // 3) Encode PDF bytes as Base64 to include in JSON response
                String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

                response.put("message", "Invoice retrieved successfully");
                response.put("invId", invId);
                response.put("pdfBase64", base64Pdf);
                return ResponseEntity.status(HttpStatus.OK).body(response);

            } catch (Exception e) {
                // DocumentException, IOException, or any other issues
            	response.put("message", "there is an error in PDF access");
            	response.put("invId", invId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
    }
    
	//@PreAuthorize("hasRole('salesManager')")  // Uncomment if security is configured
	@GetMapping("/get-all-invoices")
	public ResponseEntity<Map<String, Object>> getAllInvoicesAsPDF() {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        // 1) Fetch all invoices
	        List<Invoice> allInvoices = invoicesServices.allInvoices();
	        
	        if (allInvoices.isEmpty()) {
	            response.put("message", "No invoices found in the database");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }
	        
	        // 2) Prepare the list of invoices with Base64-encoded PDFs
	        List<Map<String, String>> invoiceList = new ArrayList<>();
	        
	        for (Invoice invoice : allInvoices) {
	            try {
					invoice.setCardHolderName(authServ.decrypt(invoice.getCardHolderName()));
					invoice.setCardNumber(authServ.decrypt(invoice.getCardNumber()));
					invoice.setShippingAddress(authServ.decrypt(invoice.getShippingAddress()));

	                byte[] pdfBytes = pdfServices.createInvoicePdfAsByteArray(invoice);
	                String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
	                
	                Map<String, String> invoiceData = new HashMap<>();
	                invoiceData.put("invId", invoice.getInvID());
	                invoiceData.put("pdfBase64", base64Pdf);
	                invoiceList.add(invoiceData);
	            } catch (Exception e) {
	                // Handle individual invoice errors gracefully
	                Map<String, String> errorData = new HashMap<>();
	                errorData.put("invId", invoice.getInvID());
	                errorData.put("error", "Failed to generate PDF for this invoice");
	                invoiceList.add(errorData);
	            }
	        }
	        
	        response.put("message", "All invoices retrieved successfully");
	        response.put("invoices", invoiceList);
	        return ResponseEntity.status(HttpStatus.OK).body(response);
	        
	    } catch (Exception e) {
	        // General error handling
	        response.put("message", "An error occurred while retrieving invoices");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	


	@GetMapping("/invoice-given-date-range")
	public ResponseEntity<Map<String, Object>> getInvoicesInDateRange(
			@RequestParam("start") String start,
			@RequestParam("end") String end) {
		Map<String, Object> response = new HashMap<>();
		try {
			// Parse input dates
			LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ISO_DATE);
			LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ISO_DATE);
			logger.info("Start date: {} and end date: {}", startDate, endDate);

			// Fetch invoices using service
			List<Invoice> invoices = invoicesServices.findInvoicesInRange(startDate, endDate);

			if (invoices.isEmpty()) {
				response.put("message", "No invoices found in the given date range");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			// Prepare the list of invoices with Base64-encoded PDFs
			List<Map<String, String>> invoiceList = new ArrayList<>();

			for (Invoice invoice : invoices) {
				try {
					invoice.setCardHolderName(authServ.decrypt(invoice.getCardHolderName()));
					invoice.setCardNumber(authServ.decrypt(invoice.getCardNumber()));
					invoice.setShippingAddress(authServ.decrypt(invoice.getShippingAddress()));

					byte[] pdfBytes = pdfServices.createInvoicePdfAsByteArray(invoice);
					String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

					Map<String, String> invoiceData = new HashMap<>();
					invoiceData.put("invId", invoice.getInvID());
					invoiceData.put("pdfBase64", base64Pdf);
					invoiceList.add(invoiceData);
				} catch (Exception e) {
					// Handle individual invoice errors gracefully
					Map<String, String> errorData = new HashMap<>();
					errorData.put("invId", invoice.getInvID());
					errorData.put("error", "Failed to generate PDF for this invoice");
					invoiceList.add(errorData);
				}
			}

			response.put("message", "Invoices retrieved successfully in the given date range");
			response.put("invoices", invoiceList);
			logger.info(response.toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			response.put("message", "An error occurred while fetching invoices in the given date range");
			response.put("error", e.getMessage());
			logger.info(response.toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}


}