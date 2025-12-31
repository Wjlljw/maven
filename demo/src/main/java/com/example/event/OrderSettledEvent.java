package com.example.event;

public class OrderSettledEvent {
    private final String itemName;
    private final int quantity;

    public OrderSettledEvent(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String itemName() {
        return itemName;
    }

    public int quantity() {
        return quantity;
    }
}
