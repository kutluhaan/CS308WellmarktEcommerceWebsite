package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.*;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.AdminServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.AuthServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductManagerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.SalesManagerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CustomerServices;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	private final CustomerServices custServ;
	
	@Autowired
	private final SalesManagerServices salesManServ;
	
	@Autowired
	private final ProductManagerServices prodManServ;

	@Autowired
	private final AdminServices adminServ;
	
	@Autowired
	private AuthServices authServ;

	@Autowired
	private JwtService jwtService;

	Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);

	public CustomerController(CustomerServices custServ, 
							  SalesManagerServices salesManServ, 
							  ProductManagerServices prodManServ,
							  AuthServices authServ,
							  JwtService jwtServ,
							  AdminServices adminServ) 
	{ this.authServ = authServ;
	  this.custServ = custServ;
	  this.jwtService = jwtServ;
	  this.salesManServ = salesManServ;
	  this.prodManServ = prodManServ; 
	  this.adminServ = adminServ; }
	
	@PostMapping("/add-customer") // working
	public ResponseEntity<Map<String, Object>> addCustomer(@Validated @RequestBody Customer customer) {
		Map<String, Object> response = new HashMap<>();
		try {
        	if ((custServ.findByEmail(customer.getEmail()) == null) && 
        		(salesManServ.findByEmail(customer.getEmail()) == null) &&
        		(prodManServ.findByEmail(customer.getEmail()) == null) && 
        		(adminServ.findByEmail(customer.getEmail()) == null)
        	) {
        		//TODO: check if the saveCustomer function actually checks for an existing e-mail.
    			String unhashedPass = customer.getPassword();
    			customer.setPassword(authServ.hashPassword(unhashedPass));
    			Customer savedCustomer = custServ.saveCustomer(customer);
            	response.put("cID", savedCustomer.getcID());
                response.put("response", "Customer successfully created");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        	} else {
        		response.put("message", "there exists a user with this email");
        		response.put("cID", customer.getcID());
        		response.put("email", customer.getEmail());
        		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        	}
        } catch (Exception e) {
        	response.put("cID", customer.getcID());
            response.put("response", "Failed to create the customer");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
		
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Customer customer = custServ.findByEmail(loginRequest.email());
		if (customer == null) {
			response.put("response", "Customer is not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		} else {
			if(!(customer.getEmail().equals(loginRequest.email()))) {
				response.put("response", "Invalid e-mail or password.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
	
			if(!(authServ.verifyPassword(loginRequest.password(), customer.getPassword()))){
				response.put("response", "Invalid e-mail or password.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
	
			String token = jwtService.generateToken(customer);
	
			if(token == null) {
				response.put("response", "Invalid token.");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
	
			if(loginRequest.cart().isPresent()){
				mergeCart(customer,loginRequest.cart().get());
			}
	
			request.getSession().setAttribute(token, customer);
			response.put("Authorization", "Bearer " + token);
			return ResponseEntity.ok().header("Authorization", "Bearer " + token).body(response);
		}
	}

	@GetMapping("/{email}")
	public ResponseEntity<Map<String, Object>> findByEmail(@PathVariable String email) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        Customer custWithEmail = custServ.findByEmail(email);

	        if (custWithEmail != null) {
	            // Customer found
	            response.put("customer", custWithEmail);
	            response.put("response", "customer found successfully");
	            return ResponseEntity.status(HttpStatus.OK).body(response);
	        } else {
	            // Customer not found
	            response.put("email", email);
	            response.put("response", "customer not found");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }
	    } catch (Exception e) {
	        response.put("email", email);
	        response.put("response", "error: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	@GetMapping("/profile/{userid}")
	public ResponseEntity<Map<String, Object>> getCustomerProfile(@PathVariable String userid) {
		Map<String, Object> response = new HashMap<>();
		Customer customer = custServ.findById(userid);
		response.put("customer", customer);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@GetMapping("/profile/cart/{userid}")
	public ResponseEntity<Map<String, Object>> getCustomerCart(@PathVariable String userid) {
		Map<String, Object> response = new HashMap<>();
		Customer customer = custServ.findById(userid);
		response.put("cart", customer.getCart());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/details")
	public ResponseEntity<Map<String, Object>> getCustomerDetails(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
		token = token.substring(7);
		Customer customer = custServ.findById(jwtService.getIDFromToken(token));
		response.put("customer", customer);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/details/cart")
	public ResponseEntity<Map<String, Object>> getCustomerDetailsCart(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
		token = token.substring(7);
		Customer customer = custServ.findById(jwtService.getIDFromToken(token));
		response.put("cart", customer.getCart());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/give-rating")
	public ResponseEntity<Map<String, Object>> giveRating(@RequestBody RatingRequest request) {
	    logger.info("Received RatingRequest: pID={}, rating={}", request.pID(), request.rating());
		Double rate = custServ.giveRating(request.pID(), request.rating().orElse((double) 0));
	    Map<String, Object> response = new HashMap<>();
		if (rate != (double) -1.0) {
            response.put("object", request.pID() + " " + request.rating().toString());
            response.put("message", "rating succesfully added to product");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
		} else {
            response.put("object", request.pID() + " " + request.rating().toString());
            response.put("message", "error in giving rating");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/set-cart")
	public ResponseEntity<Map<String, Object>> setCart(HttpServletRequest request, @RequestBody CartRequest cart) {
		Map<String, Object> response = new HashMap<>();
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
		token = token.substring(7);
		Customer customer = custServ.findById(jwtService.getIDFromToken(token));
		customer.setCart(cart.cart());
		custServ.saveCustomer(customer);
		response.put("cart", customer.getCart());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}


	public void mergeCart(Customer customer, List<CartItem> anonCart) {
		if (customer.getCart() == null || customer.getCart().isEmpty()) {
			customer.setCart(anonCart);
			custServ.saveCustomer(customer);
			return;
		}
		List<CartItem> finalCart = Stream.concat(customer.getCart().stream(), anonCart.stream())
			.collect(Collectors.toMap(
				CartItem::productID,
				CartItem::quantity,
				Integer::sum
			))
			.entrySet().stream()
			.map(entry -> new CartItem(entry.getKey(), entry.getValue()))
			.collect(Collectors.toList());
		customer.setCart(finalCart);
		custServ.saveCustomer(customer);
	}

	@GetMapping("/products/check-bought/{pID}")
	public ResponseEntity<Map<String, Object>> checkBought(HttpServletRequest request, @PathVariable String pID) {
		logger.info("Got into the endpoint");
		Map<String, Object> response = new HashMap<>();
		try {
			// Extract the Authorization header

			//yorum satırı icindeki githubtan gelen code bunu muhtemelen silmemiz lazım

            /*String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                response.put("message", "Unauthorized: Missing or invalid auth token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            logger.info("token is correct");
            // Parse the token to get the customer ID
            token = token.substring(7);
            String cID = jwtService.getIDFromToken(token);
            logger.info("cID: "+ cID);
            Customer customer = custServ.findById(cID);
            List<Order> orders = customer.getOrders();
            boolean isBought = false;
            for (Order order: orders) {
            	if (order.getProductID().equals(pID)) {
            		isBought = true;
            	}
            }
            logger.info("Did it bought? "+ (isBought==false ? "no": "yes"));
            if (isBought) {
                response.put("message", "The product is bought by the customer.");
                response.put("isBought", true);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "You need to buy this product to comment or rate it.");
                response.put("isBought", false);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }*/

			String token = request.getHeader("Authorization");
			if (token == null || !token.startsWith("Bearer ")) {
				response.put("message", "Unauthorized: Missing or invalid auth token.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}
			logger.info("Token is correct");

			// Parse the token to get the customer ID
			token = token.substring(7);
			String cID = jwtService.getIDFromToken(token);
			logger.info("cID: " + cID);

			Customer customer = custServ.findById(cID);
			List<Order> orders = customer.getOrders();
			boolean isBought = false;

			for (Order order : orders) {
				List<Order.ProductQuantity> products = order.getProducts();
				for (Order.ProductQuantity product : products) {
					if (product.getProductId().equals(pID)) {
						isBought = true;
						break;
					}
				}
				if (isBought) {
					break;
				}
			}

			logger.info("Did it buy? " + (isBought ? "yes" : "no"));

			if (isBought) {
				response.put("message", "The product is bought by the customer.");
				response.put("isBought", true);
				return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				response.put("message", "You need to buy this product to comment or rate it.");
				response.put("isBought", false);
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}

		} catch (Exception e) {
			response.put("message", "Error occurred while processing the request.");
			response.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}



}
