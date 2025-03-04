package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Invoice;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class PdfServices {

    public String createInvoicePdf(Map<String, Object> invoiceData) throws DocumentException, IOException {
        Document document = new Document();
        String filePath = "invoice.pdf"; // PDF'in 

        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // 
        String name = (String) invoiceData.get("name");
        String surname = (String) invoiceData.get("surname");
        String cardNumber = (String) invoiceData.get("cardNumber");
        String email = (String) invoiceData.get("email");
        String address = (String) invoiceData.get("address");
        List<String> purchasedItems = (List<String>) invoiceData.get("purchasedItems");

        // 
        document.add(new Paragraph("FATURA"));
        document.add(new Paragraph(" "));

        // 
        document.add(new Paragraph("Ad: " + name));
        document.add(new Paragraph("Soyad: " + surname));
        document.add(new Paragraph("Kart Numarası: " + cardNumber));
        document.add(new Paragraph("Email: " + email));
        document.add(new Paragraph("Adres: " + address));
        document.add(new Paragraph(" "));

        // 
        PdfPTable table = new PdfPTable(1);
        PdfPCell cell = new PdfPCell(new Paragraph("Satın Alınan Ürünler"));
        cell.setColspan(1);
        table.addCell(cell);

        for (String item : purchasedItems) {
            table.addCell(item);
        }

        document.add(table);
        document.close();
        

        return filePath;
    }

    public byte[] createInvoicePdfAsByteArray(Invoice invoice) throws DocumentException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        // 1. Basic Invoice Header
        document.add(new Paragraph("INVOICE\n\n"));
        document.add(new Paragraph("Invoice ID: " + invoice.getInvID()));
        document.add(new Paragraph("Customer Name: " + invoice.getCustomerName()));
        document.add(new Paragraph("Customer Email: " + invoice.getCustomerEmail()));
        document.add(new Paragraph("Shipping Address: " + invoice.getShippingAddress()));
        document.add(new Paragraph("Purchase Date: " + invoice.getPurchaseDate()));
        document.add(new Paragraph("Card Number: " + invoice.getCardNumber()));
        document.add(new Paragraph("Card Holder: " + invoice.getCardHolderName()));
        document.add(new Paragraph("Total Amount: " + invoice.getTotalAmount() + "\n\n"));

        // 2. Line Items Table
        //    We'll use 4 columns: Product, Quantity, Price, Line Total
        PdfPTable table = new PdfPTable(4); // 4 columns
        table.setWidthPercentage(100);      // Spread the table across the page

        // Table Header Row
        table.addCell("Product");
        table.addCell("Quantity");
        table.addCell("Price");
        table.addCell("Line Total");

        // Populate table rows from each InvoiceLineItem
        if (invoice.getLineItems() != null) {
            for (Invoice.InvoiceLineItem item : invoice.getLineItems()) {
                // Product Name
                table.addCell(item.getProductName() != null ? item.getProductName() : "");
                // Quantity
                table.addCell(String.valueOf(item.getQuantity()));
                // Price
                table.addCell(String.valueOf(item.getPrice()));
                // Line Total
                table.addCell(String.valueOf(item.getLineTotal()));
            }
        }

        // Add the table to the document
        document.add(table);

        document.close();
        return out.toByteArray();
    }
}