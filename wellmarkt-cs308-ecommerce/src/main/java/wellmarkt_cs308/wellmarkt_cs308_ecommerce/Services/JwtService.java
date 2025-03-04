package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Admin;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Customer;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.ProductManager;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.SalesManager;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.WellmarktCs308EcommerceApplication;


@Service
public class JwtService {
    //TODO: update the keys to asymmetric and move to application.properties
    private final String SECRET_KEY = "correcthorsebatterystapleANDhunter2";
    SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    Logger logger = LoggerFactory.getLogger(WellmarktCs308EcommerceApplication.class);



    public String generateToken(Customer customer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", customer.getEmail());
        claims.put("cID", customer.getcID());
        claims.put("role", "customer");

        return Jwts.builder()
            .claims(claims)
            .subject(customer.getcID())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
            .signWith(key)
            .compact();
    }

    public String generateToken(ProductManager productManager) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", productManager.getEmail());
        claims.put("pmID", productManager.getPmID());
        claims.put("role", "productManager");

        return Jwts.builder()
                .claims(claims)
                .subject(productManager.getPmID())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(key)
                .compact();
    }

    public String generateToken(SalesManager salesManager) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", salesManager.getEmail());
        claims.put("smID", salesManager.getSmID());
        claims.put("role", "ROLE_salesManager");

        return Jwts.builder()
                .claims(claims)
                .subject(salesManager.getSmID())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(key)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String decode(String token) {
        return Jwts.parser().verifyWith(key).decryptWith(key).build().parse(token).toString();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.get("email", String.class);
    }

    public String getIDFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return switch (claims.get("role").toString()) {
                case "customer" -> claims.get("cID", String.class);
                case "productManager" -> claims.get("pmID", String.class);
                case "ROLE_salesManager" -> claims.get("smID", String.class);
                case "admin" -> claims.get("aID", String.class);
                default -> null;
            };
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("This JWT token is not valid / could be malformed!");
            logger.info("Here is your JWT attempt: {}", token);
            logger.info("Here is your exception: {}", e.getMessage());
            return null;
        }
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }

    public String generateToken(Admin admin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", admin.getEmail());
        claims.put("aID", admin.getaID());
        claims.put("role", "admin");

        return Jwts.builder()
                .claims(claims)
                .subject(admin.getaID())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(key)
                .compact();
    }

}

