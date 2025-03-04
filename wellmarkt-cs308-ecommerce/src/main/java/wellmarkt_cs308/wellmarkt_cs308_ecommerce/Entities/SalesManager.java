package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="sales-managers")
public class SalesManager {
	@Id
	private String smID;
	private String firstName;
	private String middleName;
	private String lastName;
	private String password;
	private LocalDateTime createdAt;
	private String email;
	
	public SalesManager(String smID, String firstName, String middleName, String lastName, String password,
			 String email) {
		super();
		this.smID = smID;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.password = password;
		this.createdAt = LocalDateTime.now();
		this.email = email;
	}
	public String getSmID() {
		return smID;
	}
	public void setSmID(String smID) {
		this.smID = smID;
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
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
