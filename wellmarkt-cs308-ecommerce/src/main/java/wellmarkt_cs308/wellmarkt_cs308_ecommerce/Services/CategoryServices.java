package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Category;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.CategoryDAO;

@Service
public class CategoryServices {
	@Autowired private final CategoryDAO catDAO;
	
	public CategoryServices(CategoryDAO catDAO) {
		this.catDAO = catDAO;
	}

	public boolean saveCategory(Category cat) {
		try {
			catDAO.save(cat); // Save the category
			return catDAO.findById(cat.getCatID()).isPresent(); // Check if it exists
		} catch (Exception e) {
			// Log the exception and return false
			System.err.println("Error saving category: " + e.getMessage());
			return false;
		}
	}

	public boolean deleteCategory(String catID) {
		try {
			catDAO.deleteById(catID); // Delete the category
			return !catDAO.findById(catID).isPresent(); // Check if it no longer exists
		} catch (Exception e) {
			// Log the exception and return false
			System.err.println("Error deleting category: " + e.getMessage());
			return false;
		}
	}
	
	public List<Category> getAllCategories(){
		return catDAO.findAll();
	}
	
	public Category getSpecificCategoryByName(String catName){
		return catDAO.findByName(catName);
	}
	
	public Category findById(String catID) {
		return catDAO.findById(catID).get();
	}
}
