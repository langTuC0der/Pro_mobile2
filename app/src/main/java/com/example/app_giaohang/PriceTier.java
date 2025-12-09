package com.example.app_giaohang;

public class PriceTier {
    private String id;
    private double fromKm; // Từ km
    private double toKm;   // Đến km
    private double price;  // Giá tiền

    // Constructor rỗng cho Firebase
    public PriceTier() { }

    public PriceTier(double fromKm, double toKm, double price) {
        this.fromKm = fromKm;
        this.toKm = toKm;
        this.price = price;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getFromKm() { return fromKm; }
    public void setFromKm(double fromKm) { this.fromKm = fromKm; }

    public double getToKm() { return toKm; }
    public void setToKm(double toKm) { this.toKm = toKm; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
