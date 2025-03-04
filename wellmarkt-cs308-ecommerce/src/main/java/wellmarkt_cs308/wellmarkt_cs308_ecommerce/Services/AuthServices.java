package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class AuthServices {

    //Can be adjusted for demo purposes.
    private static final int BCRYPT_STRENGTH = 10;

    private static final String AES_SECRET_KEY = "correcthorselock";

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    // Method for AES encryption
    public String encrypt(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Method for AES decryption
    public String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    public static void main(String[] args) {
        //TODO: Delete before release.
        /*
        PasswordEncoder testPasswordEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
        PasswordEncoder testPasswordEncoder2 = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
        String password = "hunter2";
        String hashedPass = testPasswordEncoder.encode(password);
        String hashedPass2 = testPasswordEncoder2.encode(password);
        System.out.println(hashedPass);
        System.out.println(hashedPass2);
        System.out.println(testPasswordEncoder.matches(password, hashedPass));
        System.out.println(testPasswordEncoder2.matches(password, hashedPass2));
        System.out.println(testPasswordEncoder.matches(password, hashedPass2));
        System.out.println(testPasswordEncoder2.matches(password, hashedPass));
        System.out.println(testPasswordEncoder.matches("test12", "$2a$10$WHfqGTfRpjQhKeq9vS5ZS.XGYbKx5RdXApiDdOrrEN0iSpVnVj1BG"));
*/

        try {
            AuthServices authServices = new AuthServices();

            // Test hashing and verification
            String password = "MyPassword";
            String hashedPassword = authServices.hashPassword(password);
            System.out.println("Hashed Password: " + hashedPassword);
            System.out.println("Password Match: " + authServices.verifyPassword(password, hashedPassword));

            // Test AES encryption and decryption
            String data = "MySensitiveData";
            String encryptedData = authServices.encrypt(data);
            System.out.println("Encrypted Data: " + encryptedData);
            String decryptedData = authServices.decrypt(encryptedData);
            System.out.println("Decrypted Data: " + decryptedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
