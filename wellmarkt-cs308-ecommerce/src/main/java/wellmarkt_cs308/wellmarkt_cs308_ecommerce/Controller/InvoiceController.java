package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.InvoicesServices;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

	@Autowired private final InvoicesServices invServ;
	
	public InvoiceController(InvoicesServices invServ) {
		this.invServ = invServ;
	}

	@PostMapping("/save-invoice")
	public ResponseEntity<Map<String, Object>> saveInvoice(@RequestBody Invoice invoice) {
		Map<String, Object> response = new HashMap<>();
		try {
			Invoice savedInvoice = invServ.saveInvoice(invoice);

			// Return the newly created invoice's ID
			response.put("invID", savedInvoice.getInvID());
			response.put("response", "Invoice saved successfully");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			response.put("error", "Failed to save invoice: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/save-invoices")
	public ResponseEntity<Map<String, Object>> saveInvoices(@RequestBody List<Invoice> invoices) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<Invoice> savedInvoices = invServ.saveInvoices(invoices);

			response.put("invoices", savedInvoices);
			response.put("response", "Invoices saved successfully");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			response.put("error", "Failed to save invoices: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
}
