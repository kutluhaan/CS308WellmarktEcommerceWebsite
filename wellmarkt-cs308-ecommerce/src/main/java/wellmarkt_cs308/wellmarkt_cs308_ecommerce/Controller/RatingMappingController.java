package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Customer;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.RatingMapping;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.CustomerServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.RatingMappingService;

@RestController
@RequestMapping("/api/rating-mapping")
public class RatingMappingController {
	@Autowired JwtService jwtServ;
	
	@Autowired CustomerServices custServ;
	
	@Autowired RatingMappingService rmServ;
	
	Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);
	
	@PostMapping("/save-rate-map/{pID}")
	public ResponseEntity<Map<String, Object>> saveRm(@PathVariable String pID, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		try {
			logger.info(request.toString());
			logger.info(request.getHeader("Authorization"));
			String token = request.getHeader("Authorization");
			if (token == null || !token.startsWith("Bearer ")) {
				response.put("message", "unauthorized");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}
			token = token.substring(7);
			Customer customer = custServ.findById(jwtServ.getIDFromToken(token));
			RatingMapping gotRm = rmServ.findRateMapByCustomerId(customer.getcID());
			
			if (gotRm == null) {
				RatingMapping newRm = new RatingMapping();
				List<String> pIDs = new ArrayList<>();
				pIDs.add(pID);
				newRm.setcID(customer.getcID());
				newRm.setpIDs(pIDs);
				RatingMapping savedRm = rmServ.saveRateMap(newRm);
				response.put("rmID", savedRm.getRateMapID());
				response.put("rm", savedRm);
				response.put("message", "rating mapping sucessfully created");
				logger.info(response.toString());
				return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				if (gotRm.getpIDs().contains(pID)) {
					response.put("pID", pID);
					response.put("response", "Rating already given for that product");
					logger.info(response.toString());
					return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
				} else {
					gotRm.getpIDs().add(pID);
					RatingMapping newRm = rmServ.saveRateMap(gotRm);
					response.put("rm", newRm);
					response.put("rmID", newRm.getRateMapID());
					response.put("response", "rating mapping saved successfully");
					logger.info(response.toString());
					return ResponseEntity.status(HttpStatus.OK).body(response);
				}
			}
		} catch (Exception e) {
			response.put("error", "Failed to save rating mapping: " + e.getMessage());
			logger.info(response.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@GetMapping("/get-rate-map")
	public ResponseEntity<Map<String, Object>> getRm(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		try {
			String token = request.getHeader("Authorization");
			if (token == null || !token.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}
			token = token.substring(7);
			Customer customer = custServ.findById(jwtServ.getIDFromToken(token));
			RatingMapping gotRm = rmServ.findRateMapByCustomerId(customer.getcID());
			
			if (gotRm == null) {
				response.put("rm", null);
				response.put("message", "First time to rate this product");
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
			response.put("rm", gotRm);
			response.put("message", "Successfully fetched the rating mapping");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			response.put("error", "Failed to fetch rating mapping: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
