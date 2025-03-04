package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="sales")
public class Sales {
	@Id
	private String priceID;
	private String pID;
	private String cID;
	private String seller;
	private LocalDateTime soldTime;
	private String bankStatement;
	
	public Sales(String priceID, String pID, String cID, String seller, LocalDateTime soldTime, String bankStatement) {
		super();
		this.priceID = priceID;
		this.pID = pID;
		this.cID = cID;
		this.seller = seller;
		this.soldTime = soldTime;
		this.bankStatement = bankStatement;
	}
	public String getPriceID() {
		return priceID;
	}
	public void setPriceID(String priceID) {
		this.priceID = priceID;
	}
	public String getpID() {
		return pID;
	}
	public void setpID(String pID) {
		this.pID = pID;
	}
	public String getcID() {
		return cID;
	}
	public void setcID(String cID) {
		this.cID = cID;
	}
	public String getSeller() {
		return seller;
	}
	public void setSeller(String seller) {
		this.seller = seller;
	}
	public LocalDateTime getSoldTime() {
		return soldTime;
	}
	public void setSoldTime(LocalDateTime soldTime) {
		this.soldTime = soldTime;
	}
	public String getBankStatement() {
		return bankStatement;
	}
	public void setBankStatement(String bankStatement) {
		this.bankStatement = bankStatement;
	}
}
