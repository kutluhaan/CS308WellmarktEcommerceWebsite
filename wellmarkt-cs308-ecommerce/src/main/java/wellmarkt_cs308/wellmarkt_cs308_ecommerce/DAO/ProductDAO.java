package wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;

@Repository
public interface ProductDAO extends MongoRepository<Product, String> {
	@Query("{ 'category.catName' : { $regex: ?0, $options: 'i' } }")
	List<Product> findByCategoryContains(String category);

	// method for searching by name, description, or brand
	@Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } }, { 'brand': { $regex: ?0, $options: 'i' } } ] }")
	List<Product> searchByQuery(String query);

}

