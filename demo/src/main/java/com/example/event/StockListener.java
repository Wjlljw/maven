package com.example.event;

import com.example.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StockListener {

    @Autowired private StockService stockService;

    @EventListener
    public void handleOrderSettled(OrderSettledEvent event) {
        stockService.reduceStock(event.itemName(), event.quantity());
    }
}
