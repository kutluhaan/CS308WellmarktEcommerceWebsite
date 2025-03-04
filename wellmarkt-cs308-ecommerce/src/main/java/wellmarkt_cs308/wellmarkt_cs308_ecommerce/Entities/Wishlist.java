package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "wishlists")
public class Wishlist {

	@Id
	private String customerId; // The unique ID for the customer

	@Indexed
	private List<String> products = new ArrayList<>(); // Initialize list

	public Wishlist() {}

	public Wishlist(String customerId, List<String> products) {
		this.customerId = customerId;
		this.products = products;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public List<String> getWishlist() {
		return products;
	}

	public void setWishlist(List<String> products) {
		this.products = products;
	}

	public void addProductToWishlist(String productId) {
		this.products.add(productId);
	}
}