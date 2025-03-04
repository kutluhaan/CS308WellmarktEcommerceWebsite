package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="product-managers")
public class ProductManager {
	@Id
	private String pmID;
	private String firstName;
	private String middleName;
	private String lastName;
	private String password;
	private String email;
	private LocalDateTime createdAt;
	
	public ProductManager(String pmID, String firstName, String middleName, String lastName, String password,
			String email, LocalDateTime createdAt) {
		super();
		this.pmID = pmID;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.createdAt = createdAt;
	}
	public String getPmID() {
		return pmID;
	}
	public void setPmID(String pmID) {
		this.pmID = pmID;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
