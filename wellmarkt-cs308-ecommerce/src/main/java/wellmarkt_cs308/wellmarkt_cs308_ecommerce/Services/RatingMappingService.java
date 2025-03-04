package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.RateMapDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.RatingMapping;

@Service
public class RatingMappingService {
	@Autowired private final RateMapDAO rateMapDAO;
	
	public RatingMappingService(RateMapDAO rateMapDAO) {
		this.rateMapDAO = rateMapDAO;
	}
	
	public RatingMapping saveRateMap(RatingMapping rm) {
		return rateMapDAO.save(rm);
	}
	
	public RatingMapping findRateMapById(String rmID) {
		return rateMapDAO.findById(rmID).get();
	}
	
	public RatingMapping findRateMapByCustomerId(String cID) {
		return rateMapDAO.findByCustomerId(cID);
	}
}
