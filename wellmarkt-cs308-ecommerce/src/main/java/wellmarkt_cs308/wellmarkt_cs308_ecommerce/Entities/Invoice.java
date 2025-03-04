package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="invoices")
public class Invoice {
	@Id
    private String invID;
    private String customerName;
    private String cardNumber;
    private String cardHolderName;
    private String customerEmail;
    private String shippingAddress;
    private LocalDate purchaseDate;
    private List<InvoiceLineItem> lineItems;
    private double totalAmount;


    public Invoice(String invID, String customerEmail,
                   String shippingAddress, LocalDate purcahseDate,String customerName, String cardNumber, String cardHolderName, double total) {
        this.invID = invID;
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.purchaseDate = purcahseDate;
        this.customerName = customerName;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.totalAmount = total;
        this.lineItems = new ArrayList<>();
    }

    public Invoice() {};

    // Getter and Setter for invID
    public String getInvID() {
        return invID;
    }

    public void setInvID(String invID) {
        this.invID = invID;
    }

    // Getter and Setter for customerID


    // Getter and Setter for customerName
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Getter and Setter for cardNumber
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    // Getter and Setter for cardHolderName
    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    // Getter and Setter for customerEmail
    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    // Getter and Setter for shippingAddress
    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    // Getter and Setter for purchaseDate
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    // Getter and Setter for lineItems
    public List<InvoiceLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<InvoiceLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    // Getter and Setter for totalAmount
    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public static class InvoiceLineItem {

        private String productName;
        private int quantity;
        private double price;       // price per unit
        private double lineTotal;   // price * quantity

        public InvoiceLineItem() {}

        public InvoiceLineItem(String productName, int quantity,
                               double price, double lineTotal) {

            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.lineTotal = lineTotal;
        }

        // getters & setters


        public String getProductName() {
            return productName;
        }
        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }
        public void setPrice(double price) {
            this.price = price;
        }

        public double getLineTotal() {
            return lineTotal;
        }
        public void setLineTotal(double lineTotal) {
            this.lineTotal = lineTotal;
        }
    }
}

