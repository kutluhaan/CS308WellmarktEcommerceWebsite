package wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Delivery;

@Repository
public interface DeliveryDAO extends MongoRepository<Delivery, String> {
}
