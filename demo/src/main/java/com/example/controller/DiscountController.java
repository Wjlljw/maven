package com.example.controller;

import com.example.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @GetMapping("/pay")
    public String getFinalPrice(@RequestParam double price, @RequestParam String level) {
        try {
            double finalPrice = discountService.calculateFinalPrice(price, level);
            return "计算成功！原始价格：" + price + "，等级：" + level + "，折后价：" + finalPrice;
        } catch (Exception e) {
            return "计算失败：" + e.getMessage();
        }
    }
}
