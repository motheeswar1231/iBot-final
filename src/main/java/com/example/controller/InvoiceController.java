package com.example.controller;


import com.example.model.Invoice;
import com.example.repository.InvoiceRepository;
import com.example.service.InvoiceParserService;
import com.example.service.OCRSerivce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/invoice")
@CrossOrigin(origins = "http://localhost:3000")
public class InvoiceController {

    private final OCRSerivce ocrService;
    private final InvoiceParserService parserService;
    private final InvoiceRepository invoiceRepository;
    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);


    public InvoiceController(OCRSerivce ocrService,InvoiceParserService parserService,InvoiceRepository invoiceRepository){
        this.ocrService = ocrService;
        this.parserService = parserService;
        this.invoiceRepository = invoiceRepository;
    }
    @PostMapping("/upload")
    public Invoice uploadInvoice(@RequestParam("file") MultipartFile file) throws Exception {

        // OCR text extraction
        File temp = File.createTempFile("invoice_", ".png");
        file.transferTo(temp);

        String text = ocrService.extractText(temp);

        log.info("OCR Extracted Text:");
        log.info(text);

        // Convert text → JSON object
        Invoice invoice = parserService.parseAIInvoice(text);


        System.out.println("Invoice JSON : " + invoice);
        invoiceRepository.save(invoice);

        return invoice;
    }


    @GetMapping("/")
    public String home(){
        return "Invoice OCR Bot Running";
    }

    @GetMapping("/invoices")
    public List<Invoice> getInvoices(){

        List<Invoice> invoices = invoiceRepository.findAll();

        invoices.forEach(i ->
                System.out.println("ID: " + i.getId() +
                        " Company: " + i.getCompanyName())
        );
        return invoices;
    }
    @GetMapping("/invoices/{invoiceNumber}")
    public List<Invoice> getInvoiceByNumber(@PathVariable String invoiceNumber){

        List<Invoice> invoices = invoiceRepository.findByInvoiceNumber(invoiceNumber);

        invoices.forEach(i ->
                System.out.println("Filtered -> ID: " + i.getId() +
                        " InvoiceNumber: " + i.getInvoiceNumber())
        );

        return invoices;
    }
}