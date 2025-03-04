package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="deliveries")
public class Delivery {
	@Id
	private String dID;
	private String cID;
	private List<String> pID;
	private Integer quantity;
	private Double totalPrice;
	private String deliveryAddress;
	private String status;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getdID() {
		return dID;
	}
	public void setdID(String dID) {
		this.dID = dID;
	}
	public String getcID() {
		return cID;
	}
	public void setcID(String cID) {
		this.cID = cID;
	}
	public List<String> getpID() {
		return pID;
	}
	public void setpID(List<String> pID) {
		this.pID = pID;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public Delivery(String dID, String cID, List<String> pID, Integer quantity, Double totalPrice, String deliveryAddress,
			String completionField) {
		super();
		this.dID = dID;
		this.cID = cID;
		this.pID = pID;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.deliveryAddress = deliveryAddress;
		this.status = completionField;
	}
	
	
}
