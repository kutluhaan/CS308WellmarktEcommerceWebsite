package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.util.List;
import java.util.Optional;

public record LoginRequest(
    String email,
    String password,
    Optional<List<CartItem>> cart
) {}
