package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.SalesManagerDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.SalesManager;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.ProductDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//created by deniz gürleyen

@Service
public class SalesManagerServices {

    private final SalesManagerDAO salesManDAO; 
    private final ProductDAO prodDAO;
    private final WishlistServices wishlistServices;
    private final InvoicesServices invoicesServices;
    
    @Autowired
    public SalesManagerServices(SalesManagerDAO salesManDAO, ProductDAO prodDAO, WishlistServices wishlistServices, InvoicesServices invoicesServices) {
        this.salesManDAO = salesManDAO;
        this.prodDAO = prodDAO;
        this.wishlistServices = wishlistServices;
        this.invoicesServices = invoicesServices;
    }


    public SalesManager createSalesManager(SalesManager salesMan){  // not tested
    	return salesManDAO.save(salesMan);
    }

    public boolean isSalesManagerExists(String smID) {  //not tested
    	return salesManDAO.findById(smID).isPresent();
    }


    public String setProductPriceById(String salesManagerId, String productId, double newPrice) {
        // 1) Verify that the SalesManager exists
        if (!isSalesManagerExists(salesManagerId)) {
            return ("SalesManager not found with ID: " + salesManagerId);
        }

        // 2) Retrieve the product by ID
        Product prod = prodDAO.findById(productId).get();
        if (prod == null) {
            return ("Product not found with ID: " + productId);
        }

        prod.setPrice(newPrice);
        prodDAO.save(prod);

        return "success";
    }
    
    public SalesManager findByEmail(String email) {
        return salesManDAO.findByEmail(email); 
    }
    
    public String setDiscountOnProduct(String salesManagerId, String productId, double discount) {
        // 1) Verify Manager
        if (!isSalesManagerExists(salesManagerId)) {
             return ("Sales Manager not found with ID: " + salesManagerId);
        }

        // 2) Fetch Product
        Product product = prodDAO.findById(productId).get();
        if (product == null) {
        	return ("Product not found with ID: " + productId);
        }
            

        // 3) Update discount (e.g., 20.0 means 20% discount)
        product.setDiscountPercent(discount);

        // 4) Save
        prodDAO.save(product);

        // 5) Notify all wishlists that contain this product
        wishlistServices.sendDiscountEmail(productId);

        return "success";
    }
    
    // @PreAuthorize("hasRole('ROLE_SALESMANAGER')")
    @GetMapping("/invoices/range")
    public ResponseEntity<List<Invoice>> getInvoicesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end   = LocalDate.parse(endDate);

        List<Invoice> invoices = invoicesServices.findInvoicesInRange(start, end);
        return ResponseEntity.ok(invoices);
    }

    public boolean isSalesManagerPresentEmail(String email){
        SalesManager salesman =salesManDAO.findByEmail(email);
        try {
            salesman.getEmail();
        } catch (NullPointerException ignored) {
            return false;
        }
        return true;
    }
}




