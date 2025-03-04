package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.InvoicesDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice.InvoiceLineItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InvoicesServices {
    @Autowired
    private final InvoicesDAO invDAO;
    private final EmailServices emailServices;

    public InvoicesServices(InvoicesDAO invDAO, EmailServices emailServices) {
        this.invDAO = invDAO;
        this.emailServices = emailServices;
    }

    /**
     * Saves a single Invoice to MongoDB.
     */
    public Invoice saveInvoice(Invoice inv) {
        return invDAO.save(inv);
    }

    /**
     * Saves multiple Invoices at once.
     */
    public List<Invoice> saveInvoices(List<Invoice> invoices) {
        return invDAO.saveAll(invoices);
    }

    /**
     * Retrieves all invoices from MongoDB.
     */
    public List<Invoice> allInvoices() {
        return invDAO.findAll();
    }

    public List<Invoice> findInvoicesInRange(LocalDate start, LocalDate end) {
        return invDAO.findByPurchaseDateBetween(start, end);
    }


    /**
     * Tries to find a specific Invoice by its ID.
     */
    public Invoice findInvoiceById(String invoiceId) {
        return invDAO.findById(invoiceId).orElse(null);
    }



    /**
     * Creates and stores an Invoice using a Map of data that matches
     * your new Invoice fields. This is just an example of how you might
     * transform map data into the new Invoice entity.
     */
    @SuppressWarnings("unchecked")
    public Invoice createAndStoreInvoiceFromMap(Map<String, Object> invoiceData) {
        // 1) Extract fields from the map (some might be null or missing)
        String invID           = null;  // We'll let Mongo generate it
        String customerName    = (String) invoiceData.get("customerName");
        String cardNumber      = (String) invoiceData.get("cardNumber");     // Possibly masked or partial
        String cardHolderName  = (String) invoiceData.get("cardHolderName"); // Possibly masked or partial
        String customerEmail   = (String) invoiceData.get("customerEmail");
        String shippingAddress = (String) invoiceData.get("shippingAddress");
        Double totalAmount     = (Double) invoiceData.get("totalAmount");
        if (totalAmount == null) {
            totalAmount = 0.0;
        }

        // For purchaseDate, if not provided we default to 'today'
        LocalDate purchaseDate = (LocalDate) invoiceData.get("purchaseDate");
        if (purchaseDate == null) {
            purchaseDate = LocalDate.now();
        }

        // 2) Build line items if any
        List<Map<String, Object>> rawLineItems = (List<Map<String, Object>>) invoiceData.get("lineItems");
        List<InvoiceLineItem> lineItems = new ArrayList<>();
        if (rawLineItems != null) {
            for (Map<String, Object> item : rawLineItems) {
                String productName = (String) item.get("productName");
                int quantity       = (int)    item.getOrDefault("quantity", 0);
                double price       = (double) item.getOrDefault("price", 0.0);
                double lineTotal   = (double) item.getOrDefault("lineTotal", price * quantity);

                lineItems.add(new InvoiceLineItem(productName, quantity, price, lineTotal));
            }
        }

        // 3) Construct the new Invoice object
        Invoice invoice = new Invoice(
                invID,                // let Mongo generate if null
                customerEmail,        // pass to constructor
                shippingAddress,
                purchaseDate,
                customerName,
                cardNumber,
                cardHolderName,
                totalAmount
        );

        // Set the lineItems
        invoice.setLineItems(lineItems);

        // 4) Save to Mongo
        return invDAO.save(invoice);
    }
}
