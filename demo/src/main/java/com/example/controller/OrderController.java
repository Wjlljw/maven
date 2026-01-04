package com.example.controller;

import com.example.service.SettlementService;
import com.example.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired private SettlementService settlementService;
    @Autowired private StockService stockService;

    @GetMapping("/checkout")
    public String checkout(
            @RequestParam String item,
            @RequestParam double price,
            @RequestParam int num,
            @RequestParam String level) {
        
        int currentStock = stockService.getStock(item);
        if (currentStock < num) {
            return "下单失败：库存不足，当前剩余：" + currentStock;
        }

        double finalPrice = settlementService.processOrder(item, price, num, level);
        
        return String.format("购买成功！商品：%s, 数量：%d, 实付：%.2f, 剩余库存：%d", 
                item, num, finalPrice, stockService.getStock(item));
    }
}
