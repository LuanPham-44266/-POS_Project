package com.phamvietluan.pos;

public class Order {
    private String orderId;
    private double totalPrice;
    private String dateTime;

    public Order(String orderId, double totalPrice, String dateTime) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.dateTime = dateTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getDateTime() {
        return dateTime;
    }
}
