package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.util.List;

public record CartRequest(
        List<CartItem> cart
) {
}
