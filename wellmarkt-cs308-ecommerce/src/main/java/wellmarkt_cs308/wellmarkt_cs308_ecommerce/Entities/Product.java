package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="products")
public class Product {
	@Id
	private String pID;
	private String name;
	private String description;
	private String imageURL;
	private Integer stock;
	private	Boolean isActive;
	private Double rating = 0.0;
	private Integer ratingCount = 0;
	private Category category;
	private List<Comment> comments;
	private String brand;
	private boolean display = true;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String model; // Product model
	private String serialNumber; // Serial number
	private Double price; // Price of the product
	private Boolean warrantyStatus; // Indicates if the warranty is active
    private String distributorInfo; // Distributor information				
    private Double discountPercent = 0.0;
	
    
    public boolean isDisplay() {
		return display;
	}
	public void setDisplay(boolean display) {
		this.display = display;
	}
	// is this suppose toe be here 
    public Integer getRatingCount() {
		return ratingCount;
	} 
    // why are these here and not below
	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}

	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	
	public Product() {};
	
	public Product(String pID, String name, String description, String imageURL, Integer stock, Boolean isActive, String model, 
			String serialNumber, Double price, Boolean warrantyStatus, String distributorInfo, Double rating, Category category, 
			String brand, LocalDateTime createdAt, LocalDateTime updatedAt, Integer ratingCount) {
		
		super();
		this.pID = pID;
		this.name = name;
		this.description = description;
		this.imageURL = imageURL;
		this.stock = stock;
		this.isActive = isActive;
		this.model = model;
		this.serialNumber = serialNumber;
		this.price = price;
		this.warrantyStatus = warrantyStatus;
		this.distributorInfo = distributorInfo;
		this.rating = rating;
		this.category = category;
		this.brand = brand;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.ratingCount = ratingCount;
	}
	public String getpID() {
		return pID;
	}
	public void setpID(String pID) {
		this.pID = pID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	// ***** new below
	 public String getModel() {  
	        return model;
	}

	public void setModel(String model) {
	    this.model = model;
	}
	public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
	public Double getPrice() {  // careful
	       return price;
    }

    public void setPrice(Double price) { // careful
        this.price = price;
    }

    public Boolean getWarrantyStatus() {
        return warrantyStatus;
    }

    public void setWarrantyStatus(Boolean warrantyStatus) {
        this.warrantyStatus = warrantyStatus;
    }

    public String getDistributorInfo() {
        return distributorInfo;
    }

    public void setDistributorInfo(String distributorInfo) {
        this.distributorInfo = distributorInfo;
    }
	
 // ***** new above
    
	public Double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(pID, product.pID) &&
               Objects.equals(name, product.name) &&
               Objects.equals(description, product.description) &&
               Objects.equals(imageURL, product.imageURL) &&
               Objects.equals(stock, product.stock) &&
               Objects.equals(isActive, product.isActive) &&
               Objects.equals(rating, product.rating) &&
               Objects.equals(category, product.category) &&
               Objects.equals(brand, product.brand) &&
               Objects.equals(createdAt, product.createdAt) &&
               Objects.equals(updatedAt, product.updatedAt) &&
               Objects.equals(model, product.model) && //new
               Objects.equals(serialNumber, product.serialNumber) && //new 
               Objects.equals(price, product.price) && //new
               Objects.equals(warrantyStatus, product.warrantyStatus) && //new
               Objects.equals(distributorInfo, product.distributorInfo); //new 
    }
}
