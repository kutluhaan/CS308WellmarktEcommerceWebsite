package wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Category;

@Repository
public interface CategoryDAO extends MongoRepository<Category, String> {
	@Query("{'catName': ?0}")
	Category findByName(String catName);
}
