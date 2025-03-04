package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.JwtService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{

    @Autowired
    private JwtService jwtService;
    private final String FRONTEND_URL = "http://localhost:3000";


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    //.requestMatchers("/api/sales-manager/invoices/pdf/**").hasRole("SALESMANAGER") THİS İS İMPORTANT !!! FOR PDF STUFF 

                    .requestMatchers("/api/products/**").permitAll()
                .requestMatchers("/api/sales-manager/login").permitAll() //deniz ekledi
                .requestMatchers("/api/customer/login").permitAll()
                    .requestMatchers("/api/product-manager/comments/pending-comments/approve-comment/**").permitAll()
                    .requestMatchers("/api/product-manager/comments/**").permitAll()
                    .requestMatchers("/api/purchase/cart-purchase").permitAll()
                    //.requestMatchers("/api/orders/update-status/**").permitAll()
                    .requestMatchers("/api/comment/**").permitAll() ///product-manager/comments
                //.requestMatchers("/api/customer/**").permitAll()
                .requestMatchers("/api/customer/add-customer").permitAll()
                    .requestMatchers("/api/orders/**").permitAll()
                .requestMatchers("/api/customer/details/**").permitAll()
                .requestMatchers("/api/customer/give-rating").permitAll()
                .requestMatchers("/api/customer/profile/{userid}").access(this::checkUserMatchesPath)
                .requestMatchers("/api/customer/profile/cart/{userid}").access(this::checkUserMatchesPath)
                .anyRequest().permitAll()


            )
            .httpBasic(withDefaults())
            //.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.decoder(JwtService::decode)))
            .cors(withDefaults());

        return http.build();
    }


    private AuthorizationDecision checkUserMatchesPath(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {

        String token = context.getRequest().getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return new AuthorizationDecision(false);
        }
        token = token.substring(7);
        String cid = jwtService.getIDFromToken(token);
        String requestedUserId = context.getVariables().get("userid");
        return new AuthorizationDecision(cid.equals(requestedUserId));
    }


    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(FRONTEND_URL)); // Frontend URL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
