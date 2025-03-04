package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.EmailServices;

import java.util.Map;
 
@RestController
@RequestMapping("/api/email")
public class EmailController {
	@Autowired private final EmailServices emailService;

    
    public EmailController(EmailServices emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendEmail(@RequestBody Map<String, String> emailData) {
        String to = emailData.get("to");
        String subject = emailData.get("subject");
        String text = emailData.get("text");

        emailService.sendEmail(to, subject, text);
        return "Email başarıyla gönderildi!";
    }
}
