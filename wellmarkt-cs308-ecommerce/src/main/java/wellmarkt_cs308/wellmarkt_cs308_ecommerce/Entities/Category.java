package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="category")
public class Category {
	@Id
	private String catID;
	private String catName;
	private String catImURL;
	private String catQuote;
	private String catQuoteOwner;
	
	public Category() {};
	
	public String getCatID() {
		return catID;
	}
	public void setCatID(String catID) {
		this.catID = catID;
	}
	public String getCatName() {
		return catName;
	}
	public void setCatName(String catName) {
		this.catName = catName;
	}
	public String getCatImURL() {
		return catImURL;
	}
	public void setCatImURL(String catImURL) {
		this.catImURL = catImURL;
	}
	public String getCatQuote() {
		return catQuote;
	}
	public void setCatQuote(String catQuote) {
		this.catQuote = catQuote;
	}
	public String getCatQuoteOwner() {
		return catQuoteOwner;
	}
	public void setCatQuoteOwner(String catQuoteOwner) {
		this.catQuoteOwner = catQuoteOwner;
	}
}
