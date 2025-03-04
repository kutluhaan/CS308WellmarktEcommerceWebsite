package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="ratemap")
public class RatingMapping {
	
	@Id private String rmID;
	private String cID;
	private List<String> pIDs;
	
	public String getRateMapID() {
		return rmID;
	}
	public void setRateMapID(String rateMapID) {
		this.rmID = rateMapID;
	}
	public String getcID() {
		return cID;
	}
	public void setcID(String cID) {
		this.cID = cID;
	}
	public List<String> getpIDs() {
		return pIDs;
	}
	public void setpIDs(List<String> pIDs) {
		this.pIDs = pIDs;
	}	
}
