package wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.SalesManager;

@Repository
public interface SalesManagerDAO  extends MongoRepository<SalesManager, String> {
	@Query("{ 'email' : ?0 }") 
	SalesManager findByEmail(String email);
}


