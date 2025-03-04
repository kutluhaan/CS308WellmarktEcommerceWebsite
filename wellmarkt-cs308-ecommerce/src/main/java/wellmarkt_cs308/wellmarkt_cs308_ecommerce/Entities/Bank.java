package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.util.List;

public class Bank {
	private List<CreditCart> creditcarts;

	public Bank(List<CreditCart> creditcarts) {
		super();
		this.creditcarts = creditcarts;
	}

	public List<CreditCart> getCreditcarts() {
		return creditcarts;
	}

	public void setCreditcarts(List<CreditCart> creditcarts) {
		this.creditcarts = creditcarts;
	}
}
