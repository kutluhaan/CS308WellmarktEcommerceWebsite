package wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDAO extends MongoRepository<Comment, String> {
    // Find all comments for a product that are approved
    @Query("{'pID': ?0, 'isApproved': true}")
    List<Comment> findBypIDAndIsApprovedTrue(String pID); //WORKING // Added to efficiently retrieve only approved comments for a given product

    // Find all comments that are not approved (for product manager approval)
    List<Comment> findByIsApprovedFalse();  // Added to provide product managers with a list of comments that need approv

}
