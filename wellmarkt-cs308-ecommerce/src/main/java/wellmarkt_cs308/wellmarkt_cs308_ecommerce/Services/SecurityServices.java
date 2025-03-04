package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServices {
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	
	public String encode(String input) {
		return encoder.encode(input);
	}
}
