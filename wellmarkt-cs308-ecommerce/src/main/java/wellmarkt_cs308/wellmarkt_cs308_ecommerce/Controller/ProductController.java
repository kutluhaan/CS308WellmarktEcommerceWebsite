package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Category;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CategoryServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductServices;

@RestController
@RequestMapping("/api/products")
//@CrossOrigin(origins = "http://localhost:3000") we have to take a serious look to that it has importance
public class ProductController {
	
	@Autowired
	private final ProductServices prodServ;
	
	Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);
    @Autowired
    private CategoryServices categoryServices;

    public ProductController(ProductServices prodServ) { this.prodServ = prodServ; }

	@GetMapping("/{pID}") // working
	public Product getProductById(@PathVariable String pID) {
		return prodServ.findProductById(pID);
	}
	
	@GetMapping("/all-products") // working
	public ResponseEntity<Map<String, Object>> getAllProducts(){
		Map<String, Object> response = new HashMap<>();
        try {
        	List<Product> products =  prodServ.getAllProducts();
    		response.put("products", products);
            response.put("response", "products retrieved successfully");
            logger.info(response.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
        	logger.info(e.toString());
    		response.put("products", "none");
            response.put("response", "failed to retrieve products");
            logger.info(response.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
	}
	
	@PostMapping("/add-product") // working
	public ResponseEntity<Map<String, Object>> addProduct(@Validated @RequestBody Product product) {
		Map<String, Object> response = new HashMap<>();
		try {
            // Ensure price is set, or default to 0
            if (product.getPrice() == null) {
                product.setPrice(0.0);
            }
            Product savedProduct = prodServ.saveProduct(product);
            response.put("product", savedProduct.getpID());
            response.put("response", "product created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
        	response.put("product", product.getpID());
            response.put("response", "failed to create product");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
		
	}
	
	@PostMapping("/add-products") // working
	public ResponseEntity<Map<String, Object>> addAllProducts(@RequestBody List<Product> products) {
		Map<String, Object> response = new HashMap<>();
		try {
            List<Product> savedProducts = prodServ.saveAllProducts(products);
            response.put("products", savedProducts);
            response.put("response", "products created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
        	response.put("products", products);
            response.put("response", "failed to create products");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
	}
	
	@GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(@PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = prodServ.findByCategory(category);
        response.put("products", products);
        response.put("response", "Products retrieved successfully for category: " + category);
        logger.info(response.toString());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String query) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = prodServ.searchProductsByQuery(query);
        response.put("products", products);
        response.put("response", "Products retrieved successfully for search query: " + query);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PutMapping("/update-stock/add/{pID}")
    public ResponseEntity<Map<String, Object>> addStock(@PathVariable String pID, @RequestParam Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = prodServ.findProductById(pID);
            int updatedStock = product.getStock() + quantity;
            product.setStock(updatedStock);
            prodServ.saveProduct(product);

            response.put("response", "Stock added successfully.");
            response.put("product", product);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("response", "Error occurred while adding stock.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update-stock/remove/{pID}")
    public ResponseEntity<Map<String, Object>> removeStock(@PathVariable String pID, @RequestParam Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = prodServ.findProductById(pID);
            if (product.getStock() < quantity) {
                response.put("response", "Insufficient stock to remove.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            int updatedStock = product.getStock() - quantity;
            product.setStock(updatedStock);
            prodServ.saveProduct(product);

            response.put("response", "Stock removed successfully.");
            response.put("product", product);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("response", "Error occurred while removing stock.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{pID}/base-price")
    public ResponseEntity<Map<String, Object>> getBasePrice(@PathVariable String pID) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = prodServ.findProductById(pID); 
            // If product not found, findProductById might throw or return null - handle that if needed

            // Because getPrice() is your base price:
            double basePrice = product.getPrice() != null ? product.getPrice() : 0.0;

            response.put("basePrice", basePrice);
            response.put("productId", pID);
            response.put("response", "Base price retrieved successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving base price for product {}", pID, e);
            response.put("error", "Product not found or error retrieving base price");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PutMapping("/change-category/{pID}")
    public ResponseEntity<Map<String, Object>> changeProductCategory(
            @PathVariable String pID,
            @RequestParam String newCategoryID) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Find the product
            Product product = prodServ.findProductById(pID);
            if (product == null) {
                response.put("response", "Product not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Find the new category
            Category newCategory = categoryServices.findById(newCategoryID);
            if (newCategory == null) {
                response.put("response", "Category not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Update the product's category
            product.setCategory(newCategory);

            // Save the updated product
            prodServ.saveProduct(product);

            response.put("response", "Category updated successfully");
            response.put("product", product);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("response", "Error while updating category");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
