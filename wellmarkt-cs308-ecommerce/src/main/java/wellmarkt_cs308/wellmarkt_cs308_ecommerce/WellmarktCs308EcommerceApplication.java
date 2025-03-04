package wellmarkt_cs308.wellmarkt_cs308_ecommerce;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WellmarktCs308EcommerceApplication implements CommandLineRunner {
	
	Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(WellmarktCs308EcommerceApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
        // netstat -ano | findstr :8080
        // taskkill /PID 102824 /F
	}
}