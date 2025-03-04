package wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice;

@Repository
public interface InvoicesDAO extends MongoRepository<Invoice, String> {
	// Query by issueDate between start and end (inclusive)
    @Query("{ 'purchaseDate': { $gte: ?0, $lte: ?1 } }")
    List<Invoice> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);
}
