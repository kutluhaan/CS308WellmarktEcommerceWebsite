package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="customer")
public class Customer {
	@Id
	private String cID;
	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	private String taxID = null;
	private String password;
	private String address;
	private String country;
	private String phoneNumber;
	private LocalDateTime createdAt;
	private LocalDateTime lastLogin;
	
	public String getTaxID() {
		return taxID;
	}
	public void setTaxID(String taxID) {
		this.taxID = taxID;
	}
	private Wishlist wishlist;
	private List<CartItem> cart;
	private List<Order> orders;
	
	
	
	public Customer(String cID, String firstName, String middleName, String lastName, String email, String password,
			String address, String country, String phoneNumber, LocalDateTime createdAt, LocalDateTime lastLogin,
			Wishlist wishlist, List<CartItem> cart, List<Order> orders) throws Exception {
		super();
		this.cID = cID;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.address = address;
		this.country = country;
		this.phoneNumber = phoneNumber;
		this.createdAt = createdAt;
		this.lastLogin = lastLogin;
		this.wishlist = wishlist;
		if (cart == null) {
			this.cart = new ArrayList<>();
		}else {
			this.cart = cart;
		}
		if (cart == null) {
			this.orders = new ArrayList<>();
		}else {
			this.orders = orders;
		}
	}
	
	public List<Order> getOrders() {
		return orders;
	}
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	public Wishlist getWishlist() {
		return wishlist;
	}
	public void setWishlist(Wishlist wishlist) {
		this.wishlist = wishlist;
	}
	public List<CartItem> getCart() {
		return cart;
	}
	public void setCart(List<CartItem> cart) {
		this.cart = cart;
	}
	public String getcID() {
		return cID;
	}
	public void setcID(String cID) {
		this.cID = cID;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password)  {
		this.password = password;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}


}
