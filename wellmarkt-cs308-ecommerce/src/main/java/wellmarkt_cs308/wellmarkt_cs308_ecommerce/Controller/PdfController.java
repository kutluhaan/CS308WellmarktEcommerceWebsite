package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Controller;

import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.InvoicesServices;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services.PdfServices;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/pdf")
public class PdfController {

	@Autowired private final PdfServices pdfService;
	@Autowired private final InvoicesServices invoicesServices;



    public PdfController(PdfServices pdfService, InvoicesServices invoicesServices) {
        this.pdfService = pdfService;
        this.invoicesServices = invoicesServices;
    }

    @PostMapping("/create")
    public String createPdf(@RequestBody Map<String, Object> invoiceData) {
        try {
            String filePath = pdfService.createInvoicePdf(invoiceData);
            invoicesServices.createAndStoreInvoiceFromMap(invoiceData);
            return "PDF başarıyla oluşturuldu: " + filePath;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "PDF oluşturulurken bir hata oluştu.";
        }
    }
}
