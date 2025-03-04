package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Wishlist;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.WishlistServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistServices wishlistService;

    @Autowired
    private JwtService jwtService;

    Logger logger = LoggerFactory.getLogger(WishlistController.class);


    public String tokenHelper(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        logger.info("token header: {}", token);
        return (token == null || !token.startsWith("Bearer ")) ? null : token.substring(7);
    }

    // Add a product to a user's wishlist
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addProductToWishlist(@RequestParam String productId, HttpServletRequest request) {
        logger.info("wishlist add: {}", productId);
        String token = tokenHelper(request);
        logger.info("wishlist token: {}", token);
        Map<String, Object> response = new HashMap<>();
        if(token == null) {
            response.put("message", "invalid token");
            logger.error("null token on add");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String customerId = jwtService.getIDFromToken(token);
        logger.info("customerId on add: {}", customerId);
        if(customerId == null) {
            response.put("message", "invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        wishlistService.addProductToWishlist(customerId, productId);
        response.put("cID", customerId);
        response.put("message", "Product added to wishlist");
        return ResponseEntity.ok(response);
    }

    // Remove a product from a user's wishlist
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeProductFromWishlist(@RequestParam String productId, HttpServletRequest request) {
        logger.info("wishlist remove: {}", productId);
        String token = tokenHelper(request);
        Map<String, Object> response = new HashMap<>();
        if(token == null) {
            response.put("message", "invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String customerId = jwtService.getIDFromToken(token);
        if(customerId == null) {
            response.put("message", "invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        wishlistService.removeProductFromWishlist(customerId, productId);
        response.put("cID", customerId);
        response.put("message", "Product removed from the wishlist");
        return ResponseEntity.ok(response);
    }

    // Get a user's wishlist
    @GetMapping("/get-user-wishlist")
    public ResponseEntity<Map<String, Object>> getWishlistByCustomerId(HttpServletRequest request) {
        String token = tokenHelper(request);
        Map<String, Object> response = new HashMap<>();
        if(token == null) {
            response.put("message", "invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String customerId = jwtService.getIDFromToken(token);
        if(customerId == null) {
            response.put("message", "invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        Wishlist wishlist = wishlistService.getWishlistByCustomerId(customerId);
        if (wishlist == null) {
            response.put("cID", customerId);
            response.put("message", "requested wishlist is not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("cID", customerId);
        response.put("wishlist", wishlist.getWishlist());
        response.put("message", "Wishlist retrieved successfully"); // Fixed the message
        return ResponseEntity.ok(response);
    }

    // Get all users with a specific product in their wishlist
    // not sure if we need this as an endpoint
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getCustomersWithProduct(@PathVariable String productId) {
        List<Wishlist> customers = wishlistService.getCustomersWithProduct(productId);
		Map<String, Object> response = new HashMap<>();
		response.put("pID", productId);
		response.put("customers", customers);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
