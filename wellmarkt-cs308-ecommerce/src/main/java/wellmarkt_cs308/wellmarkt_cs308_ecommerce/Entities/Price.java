package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="prices")
public class Price {
	@Id
	private String priceId;
	private String smID;
	private String pID;
	private Double productPrice;
	private Integer discount;
	
	public Price(String priceId, String smID, String pID, Double productPrice, Integer discount) {
		super();
		this.priceId = priceId;
		this.smID = smID;
		this.pID = pID;
		this.productPrice = productPrice;
		this.discount = discount;
	}
	public String getPriceId() {
		return priceId;
	}
	public void setPriceId(String priceId) {
		this.priceId = priceId;
	}
	public String getSmID() {
		return smID;
	}
	public void setSmID(String smID) {
		this.smID = smID;
	}
	public String getpID() {
		return pID;
	}
	public void setpID(String pID) {
		this.pID = pID;
	}
	public double getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}
	public int getDiscount() {
		return discount;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
}
