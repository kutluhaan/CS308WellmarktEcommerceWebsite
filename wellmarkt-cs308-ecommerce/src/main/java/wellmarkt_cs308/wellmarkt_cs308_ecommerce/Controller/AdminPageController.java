package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.*;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.AdminServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.AuthServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CategoryServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CustomerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductManagerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.SalesManagerServices;

import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/admin")
public class AdminPageController {

    private static final Logger logger = LoggerFactory.getLogger(AdminPageController.class);

    @Autowired
    private AdminServices adminServices;
    
	@Autowired
	private final CustomerServices custServ;
	
	@Autowired
	private final SalesManagerServices salesManServ;
	
	@Autowired
	private final ProductManagerServices prodManServ;

    @Autowired
    private AuthServices authServices;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ProductServices productServices;
    @Autowired
    private CategoryServices categoryServices;

    @Autowired
    public AdminPageController(ProductServices productServices, 
    						   CategoryServices categoryServices,
    						   CustomerServices custServ, 
 							   SalesManagerServices salesManServ, 
 							   ProductManagerServices prodManServ,
 							   AdminServices adminServ,
 							   JwtService jwtServ,
 							   AuthServices authServ) {
        this.productServices = productServices;
        this.categoryServices = categoryServices;
        this.jwtService = jwtServ;
        this.adminServices = adminServ;
        this.authServices = authServ;
        this.custServ = custServ;
        this.salesManServ = salesManServ;
        this.prodManServ = prodManServ;
    }

    // Fetch all products
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        Map<String, Object> response = new HashMap<>();
        logger.info("Fetching all products (Admin endpoint)...");
        try {
            List<Product> products = adminServices.getAllProducts();
            response.put("products", products);
            response.put("message", "Products retrieved successfully");
            logger.info("Products retrieved successfully. Count: {}", products.size());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving products");
            response.put("error", e.getMessage());
            logger.error("Error retrieving products: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Map<String, Object> productData) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Extract category IDs
            List<String> categoryIds = (List<String>) productData.get("category");
            List<Category> categories = categoryIds.stream()
                    .map(categoryServices::findById)
                    .toList();

            // Create product object
            Product product = new Product();
            product.setName((String) productData.get("name"));
            product.setDescription((String) productData.get("description"));
            product.setImageURL((String) productData.get("imageURL"));
            product.setStock(Integer.parseInt(productData.get("stock").toString()));
            product.setActive((Boolean) productData.get("isActive"));
            product.setModel((String) productData.get("model"));
            product.setSerialNumber((String) productData.get("serialNumber"));
            product.setPrice(Double.parseDouble(productData.get("price").toString()));
            product.setWarrantyStatus((Boolean) productData.get("warrantyStatus"));
            product.setDistributorInfo((String) productData.get("distributorInfo"));
            product.setRating(Double.parseDouble(productData.get("rating").toString()));
            product.setRatingCount(Integer.parseInt(productData.get("ratingCount").toString()));
            product.setBrand((String) productData.get("brand"));

            // Handle timestamps with Z (UTC format)
            product.setCreatedAt(LocalDateTime.ofInstant(
                    Instant.parse((String) productData.get("createdAt")),
                    ZoneId.systemDefault()
            ));
            product.setUpdatedAt(LocalDateTime.ofInstant(
                    Instant.parse((String) productData.get("updatedAt")),
                    ZoneId.systemDefault()
            ));

            // Assign the first category
            if (!categories.isEmpty()) {
                product.setCategory(categories.get(0));
            }

            // Save the product
            Product savedProduct = productServices.saveProduct(product);
            response.put("product", savedProduct);
            response.put("message", "Product added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "Error adding product");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    // Delete a product by ID
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable String productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (productServices.isProductExists(productId)) {
                productServices.deleteProductById(productId);
                response.put("message", "Product deleted successfully");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "Product not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Error deleting product");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Fetch all categories
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Category> categories = categoryServices.getAllCategories();

            // Create a new list of categories without subcategories
            List<Map<String, Object>> mainCategories = new ArrayList<>();
            for (Category category : categories) {
                Map<String, Object> categoryData = new HashMap<>();
                categoryData.put("_id", category.getCatID());
                categoryData.put("catName", category.getCatName());
                categoryData.put("catImURL", category.getCatImURL());
                categoryData.put("catQuote", category.getCatQuote());
                categoryData.put("catQuoteOwner", category.getCatQuoteOwner());
                categoryData.put("_class", category.getClass().getName());
                mainCategories.add(categoryData);
            }

            response.put("categories", mainCategories);
            response.put("message", "Main categories retrieved successfully");
            logger.info("Response: " + response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving categories");
            response.put("error", e.getMessage());
            logger.error("Error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/categories/save-category")
    public ResponseEntity<Map<String, Object>> saveCategory(@RequestBody Category cat) {
        Map<String, Object> response = new HashMap<>();
        boolean saveResponse = categoryServices.saveCategory(cat); // Reusing service layer logic
        if (saveResponse) {
            response.put("catID", cat.getCatID());
            response.put("response", "Category " + cat.getCatName() + " saved successfully!");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("catID", cat.getCatID());
            response.put("response", "Failed to save the category " + cat.getCatName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/categories/delete-category/{catID}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable String catID) {
        Map<String, Object> response = new HashMap<>();
        boolean deleteResponse = categoryServices.deleteCategory(catID); // Reusing service layer logic
        if (deleteResponse) {
            response.put("catID", catID);
            response.put("response", "Category with ID " + catID + " deleted successfully!");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("catID", catID);
            response.put("response", "Failed to delete the category with ID " + catID);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/sign-up")//working
    public ResponseEntity<Map<String, Object>> createAdminAccount(@RequestBody Admin admin) {

        Map<String, Object> response = new HashMap<>();

    	if ((custServ.findByEmail(admin.getEmail()) == null) && 
    		(salesManServ.findByEmail(admin.getEmail()) == null) &&
    		(prodManServ.findByEmail(admin.getEmail()) == null) && 
    		(adminServices.findByEmail(admin.getEmail()) == null)
    	) {
            Admin savedAdmin = adminServices.createAdmin(admin);
            response.put("message", "Admin created successfully");
            response.put("aID", savedAdmin.getaID());
            logger.info("Response:", response.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    	} else {
    		response.put("message", "there exists a user with this email");
    		response.put("aID", admin.getaID());
    		response.put("email", admin.getEmail());
    		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    	}
    }

    @PostMapping("/login") //working
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 1) Find admin by email
        Admin existingAdmin = adminServices.findByEmail(loginRequest.email());
        if (existingAdmin == null || !existingAdmin.getEmail().equals(loginRequest.email()) || !existingAdmin.getPassword().equals(loginRequest.password())) {
            response.put("e-mail", loginRequest.email());
            response.put("password", loginRequest.password());
            response.put("message", "invalid e-mail or password");
            logger.info("Response:", response.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 3) Generate the JWT using your new method
        String token = jwtService.generateToken(existingAdmin);  // <-- Now using generateToken(SalesManager)
        if (token == null) {
            response.put("token", "null");
            response.put("message", "Error during JWT creation.");
            logger.info("Response:", response.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        // 4) (Optional) set an HTTP session attribute, if desired
        request.getSession().setAttribute(token, existingAdmin);

        // 5) Return the token in both body and Authorization header
        response.put("Authorization", "Bearer " + token);
        response.put("message", "succesfull login with token attached");
        logger.info("Response:", response.toString());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

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
        String msg = adminServices.setProductPriceById(managerId, productId, newPrice);

        if (Objects.equals(msg, "success")) {
            response.put("response", "Price updated successfully");
            response.put("productId", productId);
            response.put("newPrice", newPrice);
            logger.info("Response:", response.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("aID", managerId);
            response.put("error", "failed to set price");
            logger.info("Response:", response.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/set-discount") // working
    public ResponseEntity<Map<String, Object>> setDiscountOnProduct(
            @RequestParam String productId,
            @RequestParam double discount,
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
        String adminId = jwtService.getIDFromToken(token);
        logger.info("AdminID:" + adminId + ", product ID:" + productId);

        String msg = adminServices.setDiscountOnProduct(adminId, productId, discount);
        if (Objects.equals(msg, "success")) {
            response.put("response", "Discount applied successfully");
            response.put("discountPercent", discount);
            logger.info("Response: {}", response.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("aID", adminId);
            response.put("error", msg);
            logger.info("Response: {}", response.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
