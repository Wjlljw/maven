package com.example.discount.controller;

import com.example.discount.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 折扣控制器
 */
@RestController
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    /**
     * 计算最终价格
     * @param price 原始价格
     * @param level 会员等级
     * @return 计算结果
     */
    @GetMapping("/pay")
    public ResponseEntity<String> getFinalPrice(@RequestParam double price, @RequestParam String level) {
        try {
            double finalPrice = discountService.calculateFinalPrice(price, level);
            String result = "计算成功！原始价格：" + price + "，等级：" + level + "，折后价：" + finalPrice;
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("计算失败：" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}