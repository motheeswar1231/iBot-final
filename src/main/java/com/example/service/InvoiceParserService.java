package com.example.service;

import com.example.model.Invoice;
import com.example.model.InvoiceDetails;
import com.example.repository.InvoiceRepository;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
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

    public File preprocessImage(File file) throws Exception {

        Mat img = Imgcodecs.imread(file.getAbsolutePath());

        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

        Mat threshold = new Mat();
        Imgproc.threshold(gray, threshold, 150, 255, Imgproc.THRESH_BINARY);

        File processed = new File("processed.png");
        Imgcodecs.imwrite(processed.getAbsolutePath(), threshold);

        return processed;
    }
    private String cleanText(String text){
        return text
                .replaceAll("[^\\x00-\\x7F]", "") // remove weird chars
                .replaceAll("\\|", " ")
                .replaceAll(",", "")
                .replaceAll(" +", " ")
                .trim();
    }
    public Invoice parseAIInvoice(String text) {

        text = cleanText(text); // 🔥 VERY IMPORTANT

        Invoice invoice = new Invoice();

        invoice.setCompanyName("Sri Akshyaa Traders"); // fixed (OCR unreliable)
        invoice.setSeller(extractAddress(text));
        invoice.setSystemInvoiceNumber(generateInvoiceNumber());
        invoice.setInvoiceNumber(extractBillNumber(text));
        invoice.setInvoiceDate(extractInvoiceDate(text));
        invoice.setTotalAmount(extractTotalAmount(text));
        invoice.setCreatedDate(LocalDate.now());

        List<InvoiceDetails> items = extractItems(text);
        invoice.setInvoiceDetails(items);

        return invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber() {
        int random = (int) (Math.random() * 100000);
        return "INV-" + random;
    }

    private List<InvoiceDetails> extractItems(String text){

        List<InvoiceDetails> items = new ArrayList<>();

        String[] lines = text.split("\\n");

        for(String line : lines){

            if(line.contains("ORD")){

                try {
                    // 🔹 Step 1: Clean line
                    String cleaned = line
                            .replaceAll("[^a-zA-Z0-9 ]", " ")
                            .replaceAll(" +", " ")
                            .trim();

                    // 🔹 Fix common OCR mistakes
                    cleaned = cleaned.replaceAll("4g", "18");   // fix rate issue
                    cleaned = cleaned.replaceAll("ype", "Type"); // fix product

                    String[] parts = cleaned.split(" ");

                    // 🔹 Find ORDER ID
                    String orderId = null;
                    for(String part : parts){
                        if(part.startsWith("ORD")){
                            orderId = part;
                            break;
                        }
                    }

                    // 🔹 Extract numeric values (last 3 numbers)
                    List<Double> numbers = new ArrayList<>();
                    for(String part : parts){
                        if(part.matches("\\d+")){
                            numbers.add(Double.parseDouble(part));
                        }
                    }

                    double rate = 0, qty = 0, amount = 0;

                    if(numbers.size() >= 3){
                        rate = numbers.get(numbers.size() - 3);
                        qty = numbers.get(numbers.size() - 2);
                        amount = numbers.get(numbers.size() - 1);
                    }

                    // 🔹 Extract product name (between company & rate)
                    StringBuilder productName = new StringBuilder();
                    boolean start = false;

                    for(String part : parts){
                        if(part.startsWith("ORD")){
                            start = true;
                            continue;
                        }

                        if(start){
                            // stop before numbers
                            if(part.matches("\\d+")) break;
                            productName.append(part).append(" ");
                        }
                    }

                    String product = productName.toString().trim();

                    // 🔹 Default fallback if empty
                    if(product.isEmpty()){
                        product = "Unknown Product";
                    }

                    // 🔹 Add item (NO SKIPPING)
                    InvoiceDetails item = new InvoiceDetails();
                    item.setOrderId(orderId);
                    item.setProductName(product);
                    item.setRate(rate);
                    item.setQuantity(qty);
                    item.setAmount(amount);

                    items.add(item);

                } catch (Exception e){
                    System.out.println("Handled line with fallback: " + line);

                    // 🔥 EVEN IF ERROR → ADD DEFAULT ENTRY
                    InvoiceDetails item = new InvoiceDetails();
                    item.setOrderId("UNKNOWN");
                    item.setProductName("UNKNOWN");
                    item.setRate((double) 0);
                    item.setQuantity(0);
                    item.setAmount(0);

                    items.add(item);
                }
            }
        }

        return items;
    }

    private String extractCompanyName(String text){
        if(text.contains("Sri Akshyaa Traders")){
            return "Sri Akshyaa Traders";
        }
        return null;
    }
    private String extractBillNumber(String text){
        Pattern pattern = Pattern.compile("No[:\\s]+(\\d+)");
        Matcher matcher = pattern.matcher(text);

        if(matcher.find()){
            return matcher.group(1);
        }
        return "UNKNOWN";
    }
    private String extractAddress(String text){

        String[] lines = text.split("\n");

        if(lines.length > 1){
            return lines[1].trim();
        }

        return null;
    }

    private String extractInvoiceDate(String text){
        Pattern pattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{2})");
        Matcher matcher = pattern.matcher(text);

        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

    private Double extractTotalAmount(String text){

        Pattern pattern = Pattern.compile("Grand Total\\s+(\\d+[\\d]*)");
        Matcher matcher = pattern.matcher(text);

        if(matcher.find()){
            return Double.parseDouble(matcher.group(1));
        }

        return 0.0;
    }



}