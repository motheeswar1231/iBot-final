package com.example.model;

public class InvoiceDetails {

    private String name;
    private double quantity;
    private double amount;

    public InvoiceDetails(){}

    public InvoiceDetails(String name,double quantity,double amount){
        this.name = name;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}