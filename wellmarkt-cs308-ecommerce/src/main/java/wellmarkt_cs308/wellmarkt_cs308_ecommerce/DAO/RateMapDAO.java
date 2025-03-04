package wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.RatingMapping;

@Repository
public interface RateMapDAO extends MongoRepository<RatingMapping, String> {
	@Query("{ 'cID' : ?0 }")
	RatingMapping findByCustomerId(String cID);
}
