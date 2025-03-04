package wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Wishlist;

import java.util.List;

public interface WishlistDAO extends MongoRepository<Wishlist, String> {

    @Query("{ 'products': ?0 }")
    List<Wishlist> findByProductId(String productId);// Find all wishlists containing a product
}