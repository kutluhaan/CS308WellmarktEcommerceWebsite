package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.DeliveryDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Delivery;

@Service
public class DeliveryServices {
	@Autowired DeliveryDAO delDAO;
	
	public DeliveryServices(DeliveryDAO delDAO) {
		this.delDAO = delDAO;
	}
	
	public Delivery saveDelivery(Delivery delivery) {
		return delDAO.save(delivery);
	}
	
	public List<Delivery> saveDeliveries(List<Delivery> deliveries) {
		return delDAO.saveAll(deliveries);
	}
	
	public List<Delivery> allDeliveries(){
		return delDAO.findAll();
	}
	
	public Delivery findDeliveryById(String dID) {
		return delDAO.findById(dID).get();
	}
	
	public Delivery updateDelivery(String dID, Delivery delivery) {
		Delivery del = findDeliveryById(dID);
		del.setStatus(delivery.getStatus());
		del.setcID(delivery.getcID());
		del.setDeliveryAddress(delivery.getDeliveryAddress());
		del.setpID(delivery.getpID());
		del.setTotalPrice(delivery.getTotalPrice());
		del.setQuantity(delivery.getQuantity());
		return saveDelivery(del);
	}
	
	public boolean deleteDelivery(String dID) {
		if (findDeliveryById(dID) != null) {
			delDAO.deleteById(dID);
			if (findDeliveryById(dID) == null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
