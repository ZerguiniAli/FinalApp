package com.example.finalapp;

public class Product {
    private String productId;
    private String productName;
    private String quantity;

    public Product(String productId, String productName, String quantity){
        this.productId = productId;
        this.productName = productName;
        this.quantity =quantity;
    }
    public String getProductId(){return productId;}
    public String getProductName(){return productName;}
    public String getQuantity(){return quantity;}
}

