package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="comments")
public class Comment {
	@Id
	private String comID;
	private String pID;
	private String cID;
	private String pmID;
	private LocalDateTime createdAt;
	private String text;
	private Boolean isApproved = false;

	public Comment(String pID, String text, LocalDateTime createdAt) {
		this.pID = pID;
		this.createdAt = createdAt;  // Automatically set the creation timestamp when a comment is created
		this.text = text;
		this.isApproved = false; // Automatically set to false when created to require product manager approval
	}

	public String getComID() {return comID;}
	public void setComID(String comID) {
		this.comID = comID;
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
	public String getPmID() {
		return pmID;
	}
	public void setPmID(String pmID) {
		this.pmID = pmID;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isApproved() {
		return isApproved;
	}
	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}
}
