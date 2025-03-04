package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.CustomerDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.ProductDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Comment;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Customer;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;

@Service
public class CustomerServices {
	@Autowired
	private final CustomerDAO custDAO;
	
	@Autowired
	private final ProductDAO prodDAO;
	
	Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);

    public CustomerServices(CustomerDAO custDAO, ProductDAO prodDAO) {
    	this.custDAO = custDAO; 
    	this.prodDAO = prodDAO;
    }
	
	public Customer saveCustomer(Customer customer) {
        return custDAO.save(customer);
    }
	
	public Customer findByEmail(String email) {
		return custDAO.findByEmail(email);
	}
	
	public Customer findById(String cID) {
		System.out.println("this is the cıd:  " + cID);
		return custDAO.findById(cID).get();
	}
	
	public Product makeComment(Comment comment, String pID) {
		Product prod = prodDAO.findById(pID).get();
		if (prod != null) {
			List<Comment> comments = prod.getComments();
			comments.add(comment);
			prod.setComments(comments);
						
		}
		return prod; 
	}

	public Double giveRating(String pID, Double rating) {
		Optional<Product> optionalProd = prodDAO.findById(pID);
		if (optionalProd.isEmpty()) {
			logger.warn("Product with ID {} not found.", pID);
			return -1.0; // Handle product not found
		}

		Product prod = optionalProd.get();

		// Get current rating and rate count
		double currentRating = (prod.getRating() == null) ? 0.0 : prod.getRating();
		int rateCount = (prod.getRatingCount() == null) ? 0 : prod.getRatingCount();

		// Artificially consider one rating if no ratings exist yet
		if (rateCount == 0) {
			logger.info("No previous ratings, treating current rating as initial seed.");
			rateCount = 1; // Treat as if 1 artificial rating exists
		}

		logger.info("Existing rating: {} , existing ratingCount: {}, new rating: {}",
				currentRating, rateCount, rating);

		// Ensure the rating is clamped between 0 and 5
		if (rating < 0.0) rating = 0.0;
		if (rating > 5.0) rating = 5.0;

		// Recompute the average rating
		double finalRate = ((currentRating * rateCount) + rating) / (rateCount + 1);

		logger.info("New final average rating: {}", finalRate);

		// Update product's rating and rating count
		prod.setRating(finalRate);
		prod.setRatingCount(rateCount + 1);

		// Save the product
		prodDAO.save(prod);

		return finalRate;
	}


	public List<Customer> findAllCustomers() {
        return custDAO.findAll();
    }
}

