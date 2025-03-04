package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.time.LocalDate;

public class CreditCart {
	private String cartNumber;
	private LocalDate expirationDate;
	private Integer CVV;
	
	public CreditCart(String cartNumber, LocalDate expirationDate, Integer cVV) {
		super();
		this.cartNumber = cartNumber;
		this.expirationDate = expirationDate;
		CVV = cVV;
	}
	public String getCartNumber() {
		return cartNumber;
	}
	public void setCartNumber(String cartNumber) {
		this.cartNumber = cartNumber;
	}
	public LocalDate getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}
	public int getCVV() {
		return CVV;
	}
	public void setCVV(int cVV) {
		CVV = cVV;
	}
}
