
package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.CartItem;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Customer;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.*;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Order;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

	@Autowired private final PdfServices pdfService;
	@Autowired private final EmailServices emailServices;
	@Autowired private final CustomerServices customerServices;
	@Autowired private final ProductServices productServices;
	@Autowired private final JwtService jwtService;
    @Autowired private final InvoicesServices invoicesServices;
    @Autowired private AuthServices auth;

    
    public PurchaseController(PdfServices pdfService, EmailServices emailServices, CustomerServices customerServices, ProductServices productServices, JwtService jwtService, InvoicesServices invoicesServices) {
        this.pdfService = pdfService;
        this.emailServices = emailServices;
        this.customerServices = customerServices;
        this.productServices = productServices;
        this.jwtService = jwtService;
        this.invoicesServices = invoicesServices;
    }

    @PostMapping("/cart-purchase")
    public ResponseEntity<Map<String, Object>> purchaseCart(HttpServletRequest request, @RequestBody Map<String, String> purchaseData) {
        Map<String, Object> invoiceData = new HashMap<>();
        System.out.println("Received Request: " + purchaseData.toString());
        try {
            // 1. Kullanıcı bilgilerini doğrula
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(invoiceData);
            }
            token = token.substring(7);
            String customerId = jwtService.getIDFromToken(token);

            // 2. Müşteri ve sepet bilgilerini al
            Customer customer = customerServices.findById(customerId);
            List<CartItem> cartItems = customer.getCart();

            if (cartItems == null || cartItems.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invoiceData);
            }

            String cardNumberPlain = purchaseData.get("cardNumber");
            String cardHolderPlain = purchaseData.get("cardHolder");
            //String expirationDate = purchaseData.get("expirationDate");
            //String cvv = purchaseData.get("cvv");
            // Bu "address" frontend'de zaten "Street, City, Zip" şeklinde birleştirilmiş
            String mergedAddress = purchaseData.get("address");

            //String hashedCardHolder = hashService.hashValue(cardHolderPlain);
            //String hashedCardNumber = hashService.hashValue(cardNumberPlain);




            // 3. Ürün fiyatlarını al ve toplam tutarı hesapla
            double totalAmount = 0.0;
            List<String> purchasedItems = new ArrayList<>();
            List<Order.ProductQuantity> productQuantities = new ArrayList<>();
            List<Invoice.InvoiceLineItem> invoiceLineItems = new ArrayList<>();

            for (CartItem cartItem : cartItems) {
                Optional<Product> optionalProduct = productServices.findProductByIdOptional(cartItem.productID());

                if (optionalProduct.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invoiceData);
                }

                Product product = optionalProduct.get();
                int quantity = cartItem.quantity();

                // Get current price of product
                double currentPrice = product.getPrice();
                double discountedPrice = currentPrice;
                double discountPercentage = product.getDiscountPercent();
                if (discountPercentage>=0&&discountPercentage<=100) {
                    discountedPrice = discountedPrice * (((double)100-discountPercentage)/(double)100);
                }

                double itemTotal = discountedPrice * quantity;
                totalAmount += itemTotal;

                // Build info for invoice
                purchasedItems.add(product.getName() + " - Adet: " + quantity + " - Fiyat: " + itemTotal + "₺");

                // Create the Order.ProductQuantity object and set new fields
                Order.ProductQuantity pq = new Order.ProductQuantity(cartItem.productID(), quantity);
                pq.setStatus("On Process");
                pq.setPurchasePrice(discountedPrice);       // <-- New field
                pq.setPurchasedAt(LocalDateTime.now());  // <-- New field

                productQuantities.add(pq);
                Invoice.InvoiceLineItem lineItem = new Invoice.InvoiceLineItem(
                        product.getName(),
                        quantity,
                        currentPrice,
                        itemTotal
                );
                invoiceLineItems.add(lineItem);
            }

            // 4. Yeni siparişi oluştur
            Order newOrder = new Order(customerId, productQuantities);
            newOrder.setAddress(mergedAddress);
            newOrder.setHashedCardHolder(auth.encrypt(cardHolderPlain));
            newOrder.setHashedCardNumber(auth.encrypt(cardNumberPlain));
            //newOrder.setAddress(auth.encrypt(mergedAddress));

            // Optionally set an overall order date if you have that in your Order class
            // newOrder.setOrderDate(LocalDateTime.now());

            // 5. Müşterinin sipariş listesine ekleyin ve kaydedin
            List<Order> customerOrders = customer.getOrders();
            if (customerOrders == null) {
                customerOrders = new ArrayList<>();
            }
            customerOrders.add(newOrder);
            customer.setOrders(customerOrders);

            // 6. Sepeti boşalt ve müşteri verilerini güncelle
            customer.setCart(new ArrayList<>());
            customerServices.saveCustomer(customer);

            // 7. Fatura verilerini hazırlayın
            invoiceData.put("name", customer.getFirstName());
            invoiceData.put("surname", customer.getLastName());
            invoiceData.put("cardNumber", "**** **** **** "
                    + (cardNumberPlain != null && cardNumberPlain.length() >= 4
                    ? cardNumberPlain.substring(cardNumberPlain.length() - 4)
                    : "####"));
            invoiceData.put("email", customer.getEmail());
            invoiceData.put("address", mergedAddress);
            invoiceData.put("purchasedItems", purchasedItems);
            invoiceData.put("totalAmount", totalAmount);

            // 8. PDF oluştur ve e-posta gönder
            String pdfFilePath = pdfService.createInvoicePdf(invoiceData);
            emailServices.sendEmailWithAttachment(customer.getEmail(),
                    "Faturanız",
                    "Satın alımınız için teşekkür ederiz. "
                            + "Faturanızı ekte bulabilirsiniz. Toplam Tutar: "
                            + totalAmount + "₺",
                    pdfFilePath);

            // 9. Ürün stoklarını güncelle
            for (CartItem cartItem : cartItems) {
                Optional<Product> optionalProduct = productServices.findProductByIdOptional(cartItem.productID());

                if (optionalProduct.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invoiceData);
                }

                Product product = optionalProduct.get();
                int newStock = product.getStock() - cartItem.quantity();
                if (newStock < 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invoiceData);
                }
                productServices.updateStock(product.getpID(), newStock);
            }
            // new feature

            Invoice newInvoice = new Invoice(
                    null,                       // invID -> let Mongo generate
                    customer.getEmail(),        // customerEmail
                    auth.encrypt(mergedAddress),              // shippingAddress
                    LocalDate.now(),            // purchaseDate (today)
                    customer.getFirstName() + " " + customer.getLastName(), // customerName
                    auth.encrypt(cardNumberPlain),           // cardNumber (masked)
                    auth.encrypt(cardHolderPlain),           // cardHolderName (masked)
                    totalAmount                 // total
            );

            newInvoice.setLineItems(invoiceLineItems);
            if(invoicesServices.saveInvoice(newInvoice).getTotalAmount() == newInvoice.getTotalAmount()) {
                System.out.println("Invoice saved to database");
            }else{
                System.out.println("Invoice saving failed");
            }


            invoiceData.put("message",
                    "Satın alma işlemi başarıyla tamamlandı ve fatura e-posta ile gönderildi.");
            return ResponseEntity.status(HttpStatus.OK).body(invoiceData);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            invoiceData.put("message", "Satın alma işlemi sırasında bir hata oluştu.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(invoiceData);
        } catch (Exception e) {
            e.printStackTrace();
            invoiceData.put("message", "Beklenmedik bir hata oluştu.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(invoiceData);
        }
    }


}



