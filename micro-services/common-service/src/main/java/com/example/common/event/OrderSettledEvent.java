package com.example.common.event;

import java.io.Serializable;

/**
 * 订单结算事件
 * 实现Serializable接口，支持序列化，便于在网络中传输
 */
public class OrderSettledEvent implements Serializable {
    private String itemName;
    private int quantity;
    
    // 构造方法
    public OrderSettledEvent(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }
    
    // getter 方法
    public String getItemName() {
        return itemName;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    // setter 方法
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}