package wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Customer;

@Repository
public interface CustomerDAO extends MongoRepository<Customer, String> {
	@Query("{ 'email' : ?0 }")
	Customer findByEmail(String email);
}

