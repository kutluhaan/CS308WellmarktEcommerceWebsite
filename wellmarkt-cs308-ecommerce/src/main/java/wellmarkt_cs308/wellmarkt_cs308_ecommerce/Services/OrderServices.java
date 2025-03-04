package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.OrderDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Order;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServices {
    @Autowired
    private final OrderDAO orderDAO;

    public OrderServices(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }

    public Optional<Order> findOrderById(String orderId) {
        return orderDAO.findById(orderId);
    }

    public Order saveOrder(Order order) {
        return orderDAO.save(order);
    }

    public Order updateOrderStatus(String orderId, String productId, String status) {
        Optional<Order> optionalOrder = findOrderById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            boolean productFound = false;

            for (Order.ProductQuantity product : order.getProducts()) {
                if (product.getProductId().equals(productId)) {
                    product.setStatus(status); // Durumu güncelle
                    productFound = true;
                    break;
                }
            }

            if (!productFound) {
                throw new RuntimeException("Product not found with ID: " + productId + " in Order ID: " + orderId);
            }

            return saveOrder(order); // Siparişi kaydet ve geri döndür
        } else {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
    }
}
