package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.AdminDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.CategoryDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.ProductDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Admin;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Category;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServices {

    private final AdminDAO adminDAO;
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final AuthServices authServices;

    @Autowired
    private WishlistServices wishlistServices;

    @Autowired
    private InvoicesServices invoicesServices;

    @Autowired
    public AdminServices(AdminDAO adminDAO,
                         ProductDAO productDAO,
                         CategoryDAO categoryDAO,
                         AuthServices authServices) {
        this.adminDAO = adminDAO;
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
        this.authServices = authServices;
    }

    /**
     * Create (sign-up) a new Admin.
     */
    public Admin createAdmin(Admin admin) {
        admin.setAuthTime(LocalDateTime.now());
        //admin.setPassword(authServices.hashPassword(admin.getPassword())); // Ensure passwords are hashed
        return adminDAO.save(admin);
    }

    /**
     * Check if an Admin with the given ID exists.
     */
    public boolean isAdminExists(String aID) {
        return adminDAO.findById(aID).isPresent();
    }

    /**
     * Find Admin by email.
     */
    public Admin findByEmail(String email) {
        return adminDAO.findByEmail(email);
    }

    /**
     * Find Admin by ID.
     */
    public Optional<Admin> findById(String aID) {
        return adminDAO.findById(aID);
    }

    // -----------------------------------------------------------------------
    // Admin's Product Management
    // -----------------------------------------------------------------------

    /**
     * Save (create or update) a product.
     */
    public Product saveProduct(Product product) {
        return productDAO.save(product);
    }

    /**
     * Check if a product exists by ID.
     */
    public boolean isProductExists(String productId) {
        return productDAO.existsById(productId);
    }

    /**
     * Delete a product by ID.
     */
    public void deleteProductById(String productId) {
        productDAO.deleteById(productId);
    }

    /**
     * Get all products.
     */
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    /**
     * Set product price.
     */
    public boolean setProductPrice(String productId, double newPrice) {
        Optional<Product> productOpt = productDAO.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }
        Product p = productOpt.get();
        p.setPrice(newPrice);
        productDAO.save(p);
        return true;
    }

    /**
     * Set discount on a product.
     */
    public boolean setDiscountOnProduct(String productId, double discount) {
        Optional<Product> productOpt = productDAO.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }
        Product p = productOpt.get();
        p.setDiscountPercent(discount);
        productDAO.save(p);

        // Notify wishlists
        wishlistServices.sendDiscountEmail(productId);
        return true;
    }

    // -----------------------------------------------------------------------
    // Admin's Category Management
    // -----------------------------------------------------------------------

    /**
     * Save a category.
     */
    public boolean saveCategory(Category cat) {
        Category saved = categoryDAO.save(cat);
        return (saved != null);
    }

    /**
     * Delete a category by ID.
     */
    public boolean deleteCategory(String catID) {
        if (!categoryDAO.existsById(catID)) {
            return false;
        }
        categoryDAO.deleteById(catID);
        return true;
    }

    /**
     * Get all categories (if needed).
     */
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    // -----------------------------------------------------------------------
    // Admin's Invoices
    // -----------------------------------------------------------------------

    /**
     * Fetch invoices in a date range.
     */
    public List<Invoice> getInvoicesInRange(LocalDate start, LocalDate end) {
        return invoicesServices.findInvoicesInRange(start, end);
    }

    public  String setProductPriceById(String adminId, String productId, double newPrice) {
        // 1) Verify that the SalesManager exists
        if (!isAdminExists(adminId)) {
            return ("Admin not found with ID: " + adminId);
        }

        // 2) Retrieve the product by ID
        Product prod = productDAO.findById(productId).get();
        if (prod == null) {
            return ("Product not found with ID: " + productId);
        }

        prod.setPrice(newPrice);
        productDAO.save(prod);

        return "success";
    }




    public String setDiscountOnProduct(String aID, String productId, double discount) {
        // 1) Verify Manager
        if (!isAdminExists(aID)) {
            return ("Admin not found with ID: " + aID);
        }

        // 2) Fetch Product
        Product product = productDAO.findById(productId).get();
        if (product == null) {
            return ("Product not found with ID: " + productId);
        }


        // 3) Update discount (e.g., 20.0 means 20% discount)
        product.setDiscountPercent(discount);

        // 4) Save
        productDAO.save(product);

        // 5) Notify all wishlists that contain this product
        wishlistServices.sendDiscountEmail(productId);

        return "success";
    }


}
