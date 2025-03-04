package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "orders")
public class Order {
	@Id
	private String orderId;
	private String customerId;
	private List<ProductQuantity> products;
	private String orderType;
	private String oldOrderId;
	private String address;

	private String hashedCardHolder;
	private String hashedCardNumber;

	// Optional: You could also keep a timestamp for the entire Order object
	// private LocalDateTime orderDate;

	public Order(String customerId, List<ProductQuantity> products) {
		this.orderId = UUID.randomUUID().toString(); // Unique
		this.customerId = customerId;
		this.products = products;
		this.orderType = "customer_order"; // Default order type
		// this.orderDate = LocalDateTime.now(); // If you decide to track the overall order time
		this.oldOrderId = "0";
		this.address = address;
	}

	public String getHashedCardHolder() {
		return hashedCardHolder;
	}

	public void setHashedCardHolder(String hashedCardHolder) {
		this.hashedCardHolder = hashedCardHolder;
	}

	public String getHashedCardNumber() {
		return hashedCardNumber;
	}

	public void setHashedCardNumber(String hashedCardNumber) {
		this.hashedCardNumber = hashedCardNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public List<ProductQuantity> getProducts() {
		return products;
	}

	public void setProducts(List<ProductQuantity> products) {
		this.products = products;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOldOrderId() {
		return oldOrderId;
	}

	public void setOldOrderId(String oldOrderId) {
		this.oldOrderId = oldOrderId;
	}

	// Inner class to represent product and quantity with individual status,
	// plus new fields for purchase price and purchase date/time.
	public static class ProductQuantity {
		private String productId;
		private int quantity;
		private String status;
		private double purchasePrice;      // New field: product price at purchase time
		private LocalDateTime purchasedAt; // New field: date/time of purchase


		public ProductQuantity(String productId, int quantity) {
			this.productId = productId;
			this.quantity = quantity;
			this.status = "On Process"; // Default status
			// purchasePrice and purchasedAt will be set in PurchaseController
		}

		public String getProductId() {
			return productId;
		}

		public void setProductId(String productId) {
			this.productId = productId;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public double getPurchasePrice() {
			return purchasePrice;
		}

		public void setPurchasePrice(double purchasePrice) {
			this.purchasePrice = purchasePrice;
		}

		public LocalDateTime getPurchasedAt() {
			return purchasedAt;
		}

		public void setPurchasedAt(LocalDateTime purchasedAt) {
			this.purchasedAt = purchasedAt;
		}
	}
}
