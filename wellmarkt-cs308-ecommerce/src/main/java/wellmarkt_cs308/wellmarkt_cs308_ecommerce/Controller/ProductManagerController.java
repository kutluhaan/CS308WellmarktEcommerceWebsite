package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.*;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Category;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Comment;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Delivery;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.ProductManager;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.LoginRequest;

@RestController
@RequestMapping("/api/product-manager")
public class ProductManagerController {
	
	@Autowired private final ProductManagerServices prodManServ;
	
	@Autowired private final CustomerServices custServ;
	
	@Autowired private final SalesManagerServices salesManServ;

	@Autowired private final AdminServices adminServ;
	
	@Autowired private final ProductServices prodServ;
	
	@Autowired private final CommentServices comServ;
	
	@Autowired private final InvoicesServices invServ;
	
	@Autowired private final DeliveryServices delServ;
	
	@Autowired private final CategoryServices catServ;
	
	@Autowired private JwtService jwtService;

	@Autowired private AuthServices auth;
	
	Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);
	
	public ProductManagerController(ProductManagerServices prodManServ, 
									ProductServices prodServ, 
									CommentServices comServ, 
									InvoicesServices invServ,
									DeliveryServices delServ,
									CategoryServices catServ,
									SalesManagerServices salesManServ,
									CustomerServices custServ,
									AdminServices adminServ) {
		this.prodManServ = prodManServ;
		this.prodServ = prodServ;
		this.comServ = comServ;
		this.invServ = invServ;
		this.delServ = delServ;
		this.catServ = catServ;
		this.adminServ = adminServ;
		this.custServ = custServ;
		this.salesManServ = salesManServ;
	}
	
	// PRODUCT MANAGER SIGN-UP
	@PostMapping("/sign-up") // working
	public ResponseEntity<Map<String, Object>> createProductManagerAccount(@Validated @RequestBody ProductManager prodMan) {
		Map<String, Object> response = new HashMap<>();
		
        try {
        	if ((custServ.findByEmail(prodMan.getEmail()) == null) && 
        		(salesManServ.findByEmail(prodMan.getEmail()) == null) &&
        		(prodManServ.findByEmail(prodMan.getEmail()) == null) && 
        		(adminServ.findByEmail(prodMan.getEmail()) == null)
        	) {
    			prodMan.setPassword(auth.hashPassword(prodMan.getPassword()));
    			ProductManager savedProdMan = prodManServ.createProductManagerAccount(prodMan);

    			if (prodManServ.isProductManagerExists(savedProdMan.getPmID())) {
    				response.put("response", "successfully created");
    				response.put("pmID", savedProdMan.getPmID());
    				return ResponseEntity.status(HttpStatus.CREATED).body(response);
    			} else {
        			response.put("response", "failed to create");
        			response.put("pmID", prodMan.getPmID());
        			return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
    			}
        	} else {
        		response.put("message", "there exists a user with this email");
        		response.put("pmID", prodMan.getPmID());
        		response.put("email", prodMan.getEmail());
        		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        	}
        } catch (Exception e) {
        	response.put("response", "error");
            response.put("pmID", prodMan.getPmID());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
			logger.info("Response: {}", response);
		}
	} 
	
	// NOT SURE WHAT IT IS FETCHING BUT IT SEEMS IT FETCHES THE ORDER ON DELIVERY
	@GetMapping("/products/delivery-products") // working
	public ResponseEntity<Map<String, Object>> allDeliveryProducts() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<Delivery> deliveries = delServ.allDeliveries();
			if (deliveries != null && !deliveries.isEmpty()) {
				List<Product> products = new ArrayList<>();
				for (Delivery delivery: deliveries) {
					List<String> pIDs = delivery.getpID();
					for (String pID: pIDs) {
						Optional<Product> productOpt = prodServ.findProductByIdOptional(pID);
						if (productOpt.isPresent()) {
	                        products.add(productOpt.get());
	                    } else {
	                        // Log or handle the missing product case
	                        logger.info("Warning! Product with ID " + pID + " not found.");
	                    }
					}
				}
	            response.put("delivery products", products);
	            response.put("response", "delivery products retrieved successfully");
	            return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				response.put("delivery products", "none");
	            response.put("response", "there is no delivery at the moment");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (Exception e) {
			response.put("message", "error");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
		finally {
			logger.info("Response: {}", response);
		}
	}
	
	// FETCHES ALL THE PRODUCTS AVAILABLE	
	@GetMapping("/products") // working
	public ResponseEntity<Map<String, Object>> allProducts(){
		Map<String, Object> response = new HashMap<>();
		try {
            List<Product> products = prodServ.allProducts();
            if (products != null) {
                response.put("products", products);
                response.put("message", "Products retrieved successfully");
                logger.info("Response:" + response.toString());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("products", "none");
                response.put("message", "There is no product to retrieve.");
                logger.info("Response:" + response.toString());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
		} catch (Exception e) {
            response.put("message", "error");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	
	//ADDS A NEW PRODUCT TO DATABASE
	@PostMapping("/products/add-product") // working
	public ResponseEntity<Map<String, Object>> addProduct(@Validated @RequestBody Product product) {
		Map<String, Object> response = new HashMap<>();
		
        try {
        	Category cat = product.getCategory();
    		if (catServ.findById(cat.getCatID()) == null) {
        		response.put("response", "product has an invalid category");
                response.put("pID", cat.getCatName());
                logger.info("Response:" + response.toString());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    		}
        	
        	if (prodServ.isProductExists(product.getpID())) {
        		response.put("response", "product already exists");
                response.put("pID", product.getpID());
                logger.info("Response:" + response.toString());
                return ResponseEntity.status(HttpStatus.FOUND).body(response);
        	} else {
        		Product savedProduct = prodServ.saveProduct(product);
	            if (prodServ.isProductExists(savedProduct.getpID())) {
	            	response.put("response", "successfully created");
	                response.put("pID", savedProduct.getpID());
	                logger.info("Response:" + response.toString());
	                return ResponseEntity.status(HttpStatus.CREATED).body(response);
	            } else {
	            	response.put("response", "failed to create");
	            	response.put("pID", product.getpID());
	                logger.info("Response:" + response.toString());
	                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
	            }
        	}
        } catch (Exception e) {
        	response.put("response", "error");
            response.put("pID", product.getpID());
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
	} 
	
	// UPDATES THE PRODUCT W.R.T. TO ITS ID AND NEW VALUES
	@PutMapping("/products/update-product/{pID}") // working
	public ResponseEntity<Map<String, Object>> updateProductById(@PathVariable String pID, @RequestBody Product updatedProduct) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			if (prodServ.isProductExists(pID)) {
				Product existingProd = prodServ.findProductById(pID);
				if (existingProd.equals(updatedProduct)) {
					response.put("response", "there is no update, same product");
	                response.put("pID", pID);
	                logger.info("Response:" + response.toString());
	                return ResponseEntity.status(HttpStatus.CREATED).body(response);
				} else {
					Product updatedProd = prodServ.updateProductById(pID, updatedProduct);
		            if (prodServ.isProductExists(updatedProd.getpID())) {
		            	response.put("response", "successfully updated");
		                response.put("pID", updatedProd.getpID());
		                logger.info("Response:" + response.toString());
		                return ResponseEntity.status(HttpStatus.CREATED).body(response);
		            } else {
		            	response.put("response", "cannot find such a product");
		            	response.put("pID", updatedProd.getpID());
		                logger.info("Response:" + response.toString());
		                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		            }
				}
			} else {
				response.put("response", "product to be updated is not available");
            	response.put("pID", pID);
                logger.info("Response:" + response.toString());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
            
        } catch (RuntimeException e) {
        	response.put("response", "error");
            response.put("pID", pID);
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
	}
	
	// DELETES A PRODUCT BY USING ITS ID
	@DeleteMapping("/products/delete-product/{pID}") // working
	public ResponseEntity<Map<String, Object>> deleteProductById(@PathVariable String pID) {
		Map<String, Object> response = new HashMap<>();
		try {
			if (!prodServ.isProductExists(pID)) {
        		response.put("response", "product does not exists");
                response.put("pID", pID);
                logger.info("Response:" + response.toString());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        	} else {
        		prodServ.deleteProductById(pID);
	            if (!prodServ.isProductExists(pID)) {
	            	response.put("response", "successfully deleted");
	                response.put("pID", pID);
	                logger.info("Response:" + response.toString());
	                return ResponseEntity.status(HttpStatus.CREATED).body(response);
	            } else {
	            	response.put("response", "failed to delete");
	                response.put("pID", pID);
	                logger.info("Response:" + response.toString());
	                return ResponseEntity.status(HttpStatus.CREATED).body(response);
	            }
        	}
        } catch (RuntimeException e) {
        	response.put("response", "error");
            response.put("pID", pID);
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
	}
	
	
	// DELETES A CATEGORY FROM DATABASE
	@DeleteMapping("/products/delete-category/{catID}") // working
	public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable String catID) {
		Map<String, Object> response = new HashMap<>();
		try {
			Category cat = catServ.findById(catID);
			if (cat != null) {
				if (catServ.deleteCategory(catID)) {
					response.put("response", "category deleted succesfully");
	                response.put("category", catID);
	                logger.info("Response:" + response.toString());
	                return ResponseEntity.status(HttpStatus.OK).body(response);
				} else {
					response.put("response", "failed to delete the category");
	                response.put("category", catID);
	                logger.info("Response:" + response.toString());
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
				}
			} else {
				response.put("response", "this category does not exist");
                response.put("category", catID);
                logger.info("Response:" + response.toString());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
		} catch (RuntimeException e) {
			response.put("response", "error" + e.toString());
            response.put("category", catID);
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
	}
	
	// ADDS A NEW CATEGORY TO THE DATABASE
	@PostMapping("/products/add-category") // working
    public ResponseEntity<Map<String, Object>> addCategory(@RequestBody Category cat) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (catServ.saveCategory(cat)) {
            	response.put("response", "category is added");
            	response.put("category", cat);
            	logger.info("Response:" + response.toString());
            	return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
            	response.put("response", "failed to create category");
            	response.put("category", cat);
            	logger.info("Response:" + response.toString());
            	return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
            }
        } catch (RuntimeException e) {
            response.put("response", "error");
            response.put("category", cat);
            logger.error("Error adding category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

	// UPDATES THE STOCK OF A PRODUCT
	@PutMapping("/products/update-stock/{pID}") // working
	public ResponseEntity<Map<String, Object>> updateStock(@PathVariable String pID, @RequestParam Integer stock) {
		Map<String, Object> response = new HashMap<>();
		try {
			if (prodServ.isProductExists(pID)) {
				prodServ.updateStock(pID, stock);
				response.put("response", "stock is updated succesfully");
	            response.put("pID", pID);
	            logger.info("Response:" + response.toString());
	            return ResponseEntity.status(HttpStatus.CREATED).body(response);
			} else {
				response.put("response", "there is no such product to update its stock");
	            response.put("pID", pID);
	            logger.info("Response:" + response.toString());
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (RuntimeException e) {
			response.put("response", "error");
            response.put("pID", pID);
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
	}
	
	// UPDATES THE COMMENT (MAKES IT APPROVED)
	@PutMapping("/comments/pending-comments/approve-comment/{comID}") // working
	public ResponseEntity<Map<String, Object>> approveComment(@PathVariable String comID) {
		Map<String, Object> response = new HashMap<>();
		try {			
			if (comServ.isCommentExists(comID)) {
				Comment comment = comServ.findCommentByID(comID);
				Comment visibleComment = comServ.makeCommentVisible(comment);
				if (visibleComment != null) {
					response.put("response", "comment is visible now");
		            response.put("comID", comID);
		            logger.info("Response:" + response.toString());
		            return ResponseEntity.status(HttpStatus.CREATED).body(response);
				} else {
					response.put("response", "failed to make comment visible");
		            response.put("comID", comID);
		            logger.info("Response:" + response.toString());
		            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
				}
				
			} else {
				response.put("response", "comment does not exist");
	            response.put("comID", comID);
	            logger.info("Response:" + response.toString());
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (RuntimeException e) {
			response.put("response", "error");
            response.put("comID", comID);
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
	}
	
	// RETURNS ALL OF THE COMMENTS
	@GetMapping("/comments") // working
	public ResponseEntity<Map<String, Object>> allComments(){
		Map<String, Object> response = new HashMap<>();
		try {
			List<Comment> comments = comServ.allComments();
            response.put("comments", comments);
            response.put("message", "Comments retrieved successfully");
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
            response.put("message", "error");
            response.put("error", e.getMessage());
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	// DELETES A COMMENT W.R.T. ITS ID
	@DeleteMapping("/comments/delete-comment/{comID}") // working
	public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable String comID) {
		Map<String, Object> response = new HashMap<>();
		try {			
			if (comServ.isCommentExists(comID)) {
				comServ.deleteCommentById(comID);
				if (!comServ.isCommentExists(comID)) {
					response.put("response", "comment is deleted");
		            response.put("comID", comID);
		            logger.info("Response:" + response.toString());
		            return ResponseEntity.status(HttpStatus.CREATED).body(response);
				} else {
					response.put("response", "failed to delete comment");
		            response.put("comID", comID);
		            logger.info("Response:" + response.toString());
		            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
				}
			} else {
				response.put("response", "comment does not exist");
	            response.put("comID", comID);
	            logger.info("Response:" + response.toString());
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (RuntimeException e) {
			response.put("response", "error");
            response.put("comID", comID);
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	// RETURNS ALL THE INVOICES
	@GetMapping("/invoices") // working
	public ResponseEntity<Map<String, Object>> allInvoices(){
		Map<String, Object> response = new HashMap<>();
		try {
			List<Invoice> invoices = invServ.allInvoices();
            response.put("invoices", invoices);
            response.put("message", "Invoices retrieved successfully");
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
            response.put("message", "error");
            response.put("error", e.getMessage());
            logger.info("Response:" + response.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// RETURNS ALL APPROVED COMMENTS OF A PRODUCT
	@GetMapping("/comments/approved/{pID}") // WORKING
	public ResponseEntity<Map<String, Object>> getApprovedComments(@PathVariable String pID) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<Comment> approvedComments = comServ.getApprovedCommentsByProduct(pID);
			response.put("comments", approvedComments);
			response.put("message", "Approved comments retrieved successfully");
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
			response.put("message", "error");
			response.put("error", e.getMessage());
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// RETURNS ALL PENDING
	/*@GetMapping("/comments/pending") //WORKING
	public ResponseEntity<Map<String, Object>> getPendingComments() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<Comment> pendingComments = comServ.getPendingComments();
			response.put("comments", pendingComments);
			response.put("message", "Pending comments retrieved successfully");
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
			response.put("message", "error");
			response.put("error", e.getMessage());
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}*/

	@GetMapping("/comments/pending")
	public ResponseEntity<Map<String, Object>> getPendingComments() {
		Map<String, Object> response = new HashMap<>();

		try {
			List<Comment> pendingComments = comServ.getPendingComments();

			// Yorumlara product name ekleme
			List<Map<String, Object>> enrichedComments = new ArrayList<>();

			for (Comment comment : pendingComments) {
				// Yorum bilgilerini doldur
				Map<String, Object> commentData = new HashMap<>(Map.of(
						"comID", comment.getComID(),
						"pID", comment.getpID(),
						"cID", comment.getcID(),
						"createdAt", comment.getCreatedAt(),
						"text", comment.getText(),
						"approved", comment.isApproved()
				));

				// Product bilgilerini al ve adı ekle
				try {
					String productName = prodServ.findProductById(comment.getpID()).getName();
					commentData.put("productName", productName);
				} catch (Exception e) {
					commentData.put("productName", "Product not found");
				}

				// Enriched comment data ekle
				enrichedComments.add(Map.of("comment", commentData));
			}

			response.put("comments", enrichedComments);
			response.put("response", "Pending comments with product names retrieved successfully");
			return ResponseEntity.status(HttpStatus.OK).body(response);

		} catch (Exception e) {
			response.put("error", "Failed to retrieve pending comments: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}


	// RETURNS ALL PRODUCT WITH NON-ZERO PRICES 
	@GetMapping("/products/stagable-products")
	public ResponseEntity<Map<String, Object>> getNonZeroProducts(){
		Map<String, Object> response = new HashMap<>();
		List<Product> allProducts = prodServ.getAllProducts();
		List<Product> nonZeroProducts = new ArrayList<>();
		if (allProducts != null) {
			for (Product prod: allProducts) {
				if (prod.getPrice() != 0) {
					nonZeroProducts.add(prod);
				}
			}
		}
		if (nonZeroProducts.size() != 0) {
			response.put("products", nonZeroProducts);
			response.put("message", "products with prices higher than zero fetched succesfully!");
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			response.put("products", "none");
			response.put("message", "all products' prices are zero or an error occurred");
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}	
	
	@PostMapping("/login") //working
	public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, 
	                                                 HttpServletRequest request) {

	    // Create a response map we'll populate
	    Map<String, Object> response = new HashMap<>();

	    // 1) Find ProductManager by email
	    ProductManager pm = prodManServ.findByEmail(loginRequest.email());
	    if (pm == null) {
	        response.put("message", "Invalid email or password.");
	        response.put("information", loginRequest.email() + " " + loginRequest.password());
	        logger.info(response.toString());
	        logger.info("pm is null");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }

	 // Plain-text check (no hashing):
	    if (!(auth.verifyPassword(loginRequest.password(), pm.getPassword()))) {
	    	response.put("message","Invalid e-mail or password.");
	        response.put("information", loginRequest.email() + " " + loginRequest.password());
	        logger.info(response.toString());
	        logger.info("pm password is wrong");
	        logger.info("login request password: " + loginRequest.password() + ", " + "pm hashed password: " + pm.getPassword());
	    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }

	    // 3) Generate a JWT for the ProductManager
	    String token = jwtService.generateToken(pm);
	    if (token == null) {
	        response.put("message", "Error during JWT creation.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }

	    // (Optional) store manager in HttpSession if you want
	    request.getSession().setAttribute(token, pm);

	    // 4) Positive (Successful) Response
	    response.put("message", "Product Manager login successful!");
	    response.put("token", token);
		response.put("Authorization", "Bearer " + token);

	    // Return 200 OK, plus set the Authorization header with the token
	    return ResponseEntity.ok()
	            .header("Authorization", "Bearer " + token)
	            .body(response);
	}


	@DeleteMapping("/products/{productId}")
	public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable String productId) {
		Map<String, Object> response = new HashMap<>();
		try {
			if (prodServ.isProductExists(productId)) {
				prodServ.deleteProductById(productId);
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

	@PostMapping("/products")
	public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Map<String, Object> productData) {
		Map<String, Object> response = new HashMap<>();
		try {
			// Extract category IDs
			List<String> categoryIds = (List<String>) productData.get("category");
			List<Category> categories = categoryIds.stream()
					.map(catServ::findById)
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
			Product savedProduct = prodServ.saveProduct(product);
			response.put("product", savedProduct);
			response.put("message", "Product added successfully");
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			response.put("message", "Error adding product");
			response.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

}
