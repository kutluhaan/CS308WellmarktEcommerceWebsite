package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Category;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CategoryServices;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired private final CategoryServices catServ;

	Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);

	public CategoryController(CategoryServices catServ) { this.catServ = catServ; }

	@PostMapping("/save-category")
	public ResponseEntity<Map<String, Object>> saveCategory(@RequestBody Category cat) {
		Map<String, Object> response = new HashMap<>();
		boolean saveResponse = catServ.saveCategory(cat);
		if (saveResponse) {
			response.put("catID", cat.getCatID());
			response.put("response", "category " + cat.getCatName() + " saved succesfully!");
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			response.put("catID", cat.getCatID());
			response.put("response", "failed to save the category " + cat.getCatName());
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@DeleteMapping("/delete-category/{catID}")
	public ResponseEntity<Map<String, Object>> saveCategory(@PathVariable String catID) {
		Map<String, Object> response = new HashMap<>();
		boolean deleteResponse = catServ.deleteCategory(catID);
		if (deleteResponse) {
			response.put("catID", catID);
			response.put("response", "category with ID " + catID + " deleted succesfully!");
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			response.put("catID", catID);
			response.put("response", "failed to delete the category with ID" + catID);
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/get-all-categories")
	public ResponseEntity<Map<String, Object>> getAllCategories(){
		Map<String, Object> response = new HashMap<>();
		List<Category> categories = catServ.getAllCategories();
		if (categories != null) {
			response.put("categories", categories);
			response.put("response", "categories fetched succesfully!");
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			response.put("categories", "none");
			response.put("response", "failed to fetch the categories or no categories (0 element)");
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/get-category/{catName}")
	public ResponseEntity<Map<String, Object>> getCategoryByName(@PathVariable String catName) {
		Map<String, Object> response = new HashMap<>();
		Category category = catServ.getSpecificCategoryByName(catName);
		if (category != null) {
			response.put("category", category);
			response.put("response", "category fetched succesfully!");
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			response.put("category", catName);
			response.put("response", "failed to fetch the category");
			logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
