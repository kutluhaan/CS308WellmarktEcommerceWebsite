package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Delivery;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.DeliveryServices;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {
	@Autowired private final DeliveryServices delServ;
	
	public DeliveryController(DeliveryServices delServ) {
		this.delServ = delServ;
	}
	
	@GetMapping("/all") // working
	public ResponseEntity<Map<String, Object>> allDeliveries(){
		Map<String, Object> response = new HashMap<>();
		List<Delivery> deliveries = delServ.allDeliveries();
		response.put("deliveries", deliveries);
        response.put("response", "deliveries retrieved successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping("/add-delivery") // working
	public ResponseEntity<Map<String, Object>> saveDelivery(@RequestBody Delivery delivery){
		Map<String, Object> response = new HashMap<>();
		Delivery savedDel = delServ.saveDelivery(delivery);
		response.put("dID", savedDel.getdID());
        response.put("response", "delivery saved successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping("/add-deliveries") // working
	public ResponseEntity<Map<String, Object>> saveDeliveries(@RequestBody List<Delivery> deliveries){
		Map<String, Object> response = new HashMap<>();
		List<Delivery> savedDel = delServ.saveDeliveries(deliveries);
		response.put("deliveries", savedDel);
        response.put("response", "deliveries saved successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping("/{dID}") // working
	public ResponseEntity<Map<String, Object>> getDeliveryById(@PathVariable String dID){
		Map<String, Object> response = new HashMap<>();
		Delivery delivery = delServ.findDeliveryById(dID);
		if (delivery != null) {
			response.put("deliveries", delivery);
	        response.put("response", "deliveries retrieved successfully");
	        return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			response.put("deliveries", dID);
	        response.put("response", "there is no such delivery");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
	
	@DeleteMapping("/delete-delivery/{dID}")
	public ResponseEntity<Map<String, Object>> deleteDeliveryById(@PathVariable String dID){
		Map<String, Object> response = new HashMap<>();
		boolean deleted = delServ.deleteDelivery(dID);
		if (deleted == true) {
			response.put("result", deleted);
	        response.put("response", "delivery successfully deleted");
	        return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			response.put("result", deleted);
	        response.put("response", "failed to delete");
	        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
	}
}







