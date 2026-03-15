package com.example.service;

import com.example.model.Invoice;
import com.example.model.InvoiceDetails;
import com.example.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InvoiceParserService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceParserService(InvoiceRepository invoiceRepository){
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice parseAIInvoice(String text) {

        Invoice invoice = new Invoice();

        invoice.setCompanyName(extractCompanyName(text));
        invoice.setSeller(extractAddress(text));
        invoice.setInvoiceNumber(extractBillNumber(text));
        invoice.setInvoiceDate(extractInvoiceDate(text));
        invoice.setTotalAmount(extractTotalAmount(text));

        List<InvoiceDetails> items = extractItems(text);
        invoice.setInvoiceDetails(items);

        return invoiceRepository.save(invoice);
    }

    private List<InvoiceDetails> extractItems(String text){

        List<InvoiceDetails> items = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\d+)\\s+(\\d+)\\s+(\\d+\\.\\d+)");

        Matcher matcher = pattern.matcher(text);

        while(matcher.find()){

            double qty = Double.parseDouble(matcher.group(1));
            double rate = Double.parseDouble(matcher.group(2));
            double amount = Double.parseDouble(matcher.group(3));

            items.add(new InvoiceDetails("Item", qty, amount));
        }

        return items;
    }
    private String extractCompanyName(String text){

        String[] lines = text.split("\n");

        if(lines.length > 0){
            return lines[0].trim();
        }

        return null;
    }
    private String extractBillNumber(String text){

        Pattern pattern = Pattern.compile("Bill No[:\\s]+(\\d+)");
        Matcher matcher = pattern.matcher(text);

        if(matcher.find()){
            return matcher.group(1);
        }

        return null;
    }
    private String extractAddress(String text){

        String[] lines = text.split("\n");

        if(lines.length > 1){
            return lines[1].trim();
        }

        return null;
    }

    private String extractInvoiceDate(String text){

        Pattern pattern = Pattern.compile("(\\d{2}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(text);

        if(matcher.find()){
            return matcher.group(1);
        }

        return null;
    }

    private Double extractTotalAmount(String text){

        Pattern pattern = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(text);

        double max = 0;

        while(matcher.find()){
            double value = Double.parseDouble(matcher.group());

            if(value > max){
                max = value;
            }
        }

        return max;
    }




}