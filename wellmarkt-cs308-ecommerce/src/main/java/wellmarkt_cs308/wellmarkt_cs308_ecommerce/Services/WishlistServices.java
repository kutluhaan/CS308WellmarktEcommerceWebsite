package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.WishlistDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Customer;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Wishlist;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistServices {

    @Autowired
    private WishlistDAO wishlistRepository;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private CustomerServices customerServices;

    @Autowired
    private ProductServices productServices;

    Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);


    // Add a product to a user's wishlist
    public void addProductToWishlist(String customerId, String productId) {
        Optional<Wishlist> existingWishlist = wishlistRepository.findById(customerId);
        Product prod = productServices.findProductById(productId);
        if (prod == null) {
        	return;
        }
        if (existingWishlist.isPresent()) {
            Wishlist wishlist = existingWishlist.get();
            if (!wishlist.getWishlist().contains(productId)) {
                wishlist.getWishlist().add(productId);
                wishlistRepository.save(wishlist);
            }
        } else {
            Wishlist wishlist = new Wishlist();
            wishlist.setCustomerId(customerId);
            wishlist.addProductToWishlist(productId);
            wishlistRepository.save(wishlist);
        }
    }

    // Remove a product from a user's wishlist
    public void removeProductFromWishlist(String customerId, String productId) {
        Optional<Wishlist> existingWishlist = wishlistRepository.findById(customerId);
        Product prod = productServices.findProductById(productId);
        if (prod == null) {
        	return;
        }
        if (existingWishlist.isPresent()) {
            Wishlist wishlist = existingWishlist.get();
            wishlist.getWishlist().remove(productId);
            wishlistRepository.save(wishlist);
        }
    }

    // Get all customers with a specific product in their wishlist
    public List<Wishlist> getCustomersWithProduct(String productId) {
        return wishlistRepository.findByProductId(productId);
    }

    // Get a user's wishlist
    public Wishlist getWishlistByCustomerId(String customerId) {
        return wishlistRepository.findById(customerId).orElse(null);
    }

    public void sendDiscountEmail(String productId) {
        List<Wishlist> wishlists = getCustomersWithProduct(productId);
        Product product = productServices.findProductById(productId);
        
        double discountPercent = product.getDiscountPercent() == null ? 0.0 : product.getDiscountPercent();
        double originalPrice = product.getPrice() == null ? 0.0 : product.getPrice();
        double discountedPrice = originalPrice * (1 - discountPercent / 100);

        logger.info("we are doing emails for wishlist!");
        logger.info("product id: {}", productId);
        logger.info("wishlists size: {}", wishlists.size());

        wishlists.forEach(wishlist -> {
            logger.info(wishlist.toString());
            Customer customer = customerServices.findById(wishlist.getCustomerId());

            // Dynamically generate the email content
            String emailContent = String.format(
                    """
                            Hi %s,
                            
                            Great news! An item from your wishlist has a new discount of %.2f%%.
                            
                            Product: %s
                            Original Price: $%.2f
                        	Discounted Price: $%.2f
                            View Product: http://localhost:3000/product/%s
                            
                            Don’t miss out! There is currently %s in stock!
                            Visit our website now to grab the deal before it’s gone.
                            
                            
                            Happy shopping,
                            The Wellmarkt Team""",
                    customer.getFirstName(),
                    discountPercent,
                    product.getName(),
                    originalPrice,
                    discountedPrice,
                    productId,
                    product.getStock()
            );

            emailServices.sendEmail(
                    customer.getEmail(),
                    "Discount Alert: Your Wishlist Item is on Sale!",
                    emailContent
            );
        });
    }


}
