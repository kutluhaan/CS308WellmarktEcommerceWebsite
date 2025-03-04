package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Comment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.CommentDAO;

@Service
public class CommentServices {
	@Autowired CommentDAO comDAO;
	
	public CommentServices(CommentDAO comDAO) {
		this.comDAO = comDAO;
	}
	
	public Comment saveComment(Comment com) {
		return comDAO.save(com);
	}
	
	public List<Comment> saveComments(List<Comment> coms) {
		return comDAO.saveAll(coms);
	}
	
	public Comment findCommentByID(String comID) {
		return comDAO.findById(comID).get();
	}
	
	public boolean isCommentExists(String comID) {
		return comDAO.findById(comID).isPresent();
	}
	
	public Comment makeCommentVisible(Comment comment) {
		comment.setApproved(true);
		return comDAO.save(comment);
	}

	public Comment updateApprovalStatus(String comID, boolean isApproved) {
		Comment comment = comDAO.findById(comID).orElseThrow(() ->
				new IllegalArgumentException("Comment with ID " + comID + " not found"));
		comment.setApproved(isApproved);
		return comDAO.save(comment);
	}
	
	public void deleteCommentById(String comID) {
		comDAO.deleteById(comID);
	}
	
    public List<Comment> allComments() {
        return comDAO.findAll();
    }

	public List<Comment> getPendingComments() { //WORKING
		return comDAO.findByIsApprovedFalse();  // Use the custom query to get unapproved comments, allowing product managers to easily find comments that need approval
	}

	public List<Comment> getApprovedCommentsByProduct(String pID) { //WORKING
		return comDAO.findBypIDAndIsApprovedTrue(pID);  // Use the custom query to get approved comments for a specific product, ensuring users only see approved comments
	}

}
