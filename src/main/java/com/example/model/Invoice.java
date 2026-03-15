package com.example.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;


@Entity
@Table(name = "invoice_table")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name="seller")
    private String seller;

    @Column(name = "invoice_date")
    private String invoiceDate;

    @Column(name = "total_amount")
    private double totalAmount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "item_details", columnDefinition = "jsonb")
    private List<InvoiceDetails> invoiceDetails;

    public List<InvoiceDetails> getInvoiceDetails() {
        return invoiceDetails;
    }

    public void setInvoiceDetails(List<InvoiceDetails> invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }

    public Long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

     public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getSeller() {
        return seller;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}