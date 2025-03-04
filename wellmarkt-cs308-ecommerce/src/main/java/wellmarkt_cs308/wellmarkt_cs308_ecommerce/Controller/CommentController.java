package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Comment;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CommentServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.ProductServices;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
	@Autowired private final CommentServices comServ;
	@Autowired private JwtService jwtService;
	@Autowired private final ProductServices productServices;
	
	Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);
	
	public CommentController(CommentServices comServ, JwtService jwtService, ProductServices productServices ) {
		this.comServ = comServ;
		this.jwtService = jwtService;
		this.productServices = productServices;
	}
	
	
	@PostMapping("/add-comment") // working
	public ResponseEntity<Map<String, Object>> makeComment(@RequestBody Comment comment, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		comment.setApproved(false);  // Automatically set to false when creating a new comment to ensure that it must be approved by a product manager before becoming visible
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
		token = token.substring(7);
		comment.setcID(jwtService.getIDFromToken(token));
		Comment savedComment = comServ.saveComment(comment);
        response.put("comID", savedComment.getComID());
        response.put("response", "comment saved successfully");
        logger.info("Response:" + response.toString());
        return ResponseEntity.status(HttpStatus.OK).body(response); 
	}
	
	@PostMapping("/add-comments") // working
	public ResponseEntity<Map<String, Object>> makeComment(@RequestBody List<Comment> comments) {
		Map<String, Object> response = new HashMap<>();
		List<Comment> savedComments = comServ.saveComments(comments);
        response.put("comments", savedComments);
        response.put("response", "comment saved successfully");
        logger.info("Response:" + response.toString());
        return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	//retrieve all approved comments for a specific product
	@GetMapping("/product/{pID}/approved-comments") //WORKING
	public ResponseEntity<Map<String, Object>> getApprovedComments(@PathVariable String pID) {
		Map<String, Object> response = new HashMap<>();
		List<Comment> approvedComments = comServ.getApprovedCommentsByProduct(pID);  // Retrieve only approved comments for the specified product
		logger.info("Comments:", approvedComments.toString());
		response.put("comments", approvedComments);
		response.put("response", "Approved comments retrieved successfully");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping("/approve/{comID}")
	public ResponseEntity<Map<String, Object>> updateCommentApproval(
			@PathVariable String comID,
			@RequestParam boolean isApproved) {
		Map<String, Object> response = new HashMap<>();
		try {
			Comment updatedComment = comServ.updateApprovalStatus(comID, isApproved);
			response.put("comment", updatedComment);
			response.put("response", "Comment approval status updated successfully");
	        logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			response.put("error", "Failed to update comment approval status: " + e.getMessage());
	        logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}



	@GetMapping("/pending-comments")
	public ResponseEntity<Map<String, Object>> getPendingComments() {
		Map<String, Object> response = new HashMap<>();

		try {
			List<Comment> pendingComments = comServ.getPendingComments();


			// Yorumlara product name ekleme
			List<Map<String, Object>> enrichedComments = new ArrayList<>();

			for (Comment comment : pendingComments) {
				Map<String, Object> commentData = new HashMap<>(Map.of(
						"comID", comment.getComID(),
						"pID", comment.getpID(),
						"cID", comment.getcID(),
						"createdAt", comment.getCreatedAt(),
						"text", comment.getText(),
						"approved", comment.isApproved()
				));
				System.out.println("\n****** ı am working");

				// Product bilgilerini al ve adı ekle
				try {
					String productName = productServices.findProductById(comment.getpID()).getName();
					System.out.println("\n****** " + productName);
					commentData.put("productName", productName);
				} catch (Exception e) {
					commentData.put("productName", "Product not found"); // Ürün bulunamazsa hata yakalanır
				}

				enrichedComments.add(Map.of("comment", commentData));
			}

			response.put("comments", enrichedComments);
			response.put("response", "Pending comments with product names retrieved successfully");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			response.put("error", "Failed to retrieve pending comments: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}



	@DeleteMapping("/delete-comment/{comID}")
	public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable String comID) {
		Map<String, Object> response = new HashMap<>();
		try {
            comServ.deleteCommentById(comID);
			response.put("commentID", comID);
			response.put("response", "Comment deleted successfully");
	        logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
			response.put("commentID", comID);
			response.put("error", "Failed to delete comment: " + e.getMessage());
	        logger.info("Response:" + response.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
