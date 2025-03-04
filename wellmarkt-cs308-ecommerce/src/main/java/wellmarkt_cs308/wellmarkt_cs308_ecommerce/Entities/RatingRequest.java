package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.util.Optional;

public record RatingRequest (
	String pID,
	Optional<Double> rating
	) {}
	
