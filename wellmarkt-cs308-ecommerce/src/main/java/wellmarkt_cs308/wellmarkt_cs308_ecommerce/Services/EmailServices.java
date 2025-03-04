package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;


import java.io.File;

@Service
public class EmailServices {
	
	private JavaMailSender mailSender;

    @Autowired
    public EmailServices(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("sarballmertefe@gmail.com"); // 

        mailSender.send(message);
    }
    
    public void sendEmailWithAttachment(String to, String subject, String text, String filePath) {
    	jakarta.mail.internet.MimeMessage message1 = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message1, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.setFrom("sarballmertefe@gmail.com");

            // PDF 
            File file = new File(filePath);
            helper.addAttachment(file.getName(), file);

            mailSender.send(message1);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("E-posta gönderiminde bir hata oluştu.");
        }
    }
}
