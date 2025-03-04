package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;


import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.ProductManagerDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.ProductManager;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.ProductDAO;

@Service
public class ProductManagerServices {
	@Autowired private final ProductManagerDAO prodManDAO;
	
	public ProductManagerServices(ProductManagerDAO prodManDAO, ProductDAO prodDAO) {
		this.prodManDAO = prodManDAO;
	}
	
	public ProductManager createProductManagerAccount(ProductManager prodMan) { // working
		return prodManDAO.save(prodMan);
	}
	
	public boolean isProductManagerExists(String pmID) {
		return prodManDAO.findById(pmID).isPresent();
	}
	 public ProductManager findByEmail(String email) {
	       return prodManDAO.findByEmail(email);
	    }
}
