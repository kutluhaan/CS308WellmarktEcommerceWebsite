package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="admins")
public class Admin {
	@Id
	private String aID;
	private String firstName;
	private String middleName;
	private String lastName;
	private LocalDateTime authTime;
	private String email;
	private String password;
	
	public Admin(String aID, String firstName, String middleName, String lastName, LocalDateTime authTime, String email,
			String password) {
		super();
		this.aID = aID;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.authTime = authTime;
		this.email = email;
		this.password = password;
	}
	public String getaID() {
		return aID;
	}
	public void setaID(String aID) {
		this.aID = aID;
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
	public LocalDateTime getAuthTime() {
		return authTime;
	}
	public void setAuthTime(LocalDateTime authTime) {
		this.authTime = authTime;
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
}
