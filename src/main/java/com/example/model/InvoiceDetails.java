package com.example.model;

public class InvoiceDetails {

    private String name;
    private double quantity;
    private double amount;
    private String orderId;
    private String productName;
    private double rate;
    public InvoiceDetails(){}

    public InvoiceDetails(String name,double quantity,double amount,String orderId,String productName,Double rate){
        this.name = name;
        this.quantity = quantity;
        this.amount = amount;
        this.orderId = orderId;
        this.productName = productName;
        this.rate = rate;
    }

    public String getOrderId(){return orderId;}
    public String getProductName(){return productName;}
    public double getRate(){return rate;}

    public void setOrderId(String orderId){this.orderId=orderId;}
    public void setProductName(String productName){this.productName = productName;}
    public void setRate (Double rate){this.rate = rate;}

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