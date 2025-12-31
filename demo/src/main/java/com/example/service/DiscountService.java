package com.example.service;

import com.example.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {
    public double calculateFinalPrice(double price, String memberLevel) {
        if (price < 0) {
            throw new BusinessException("金额不能为负数");
        }

        double finalPrice = price;

        if ("GOLD".equals(memberLevel)) {
            finalPrice = price * 0.8;
        } else if ("SILVER".equals(memberLevel)) {
            finalPrice = price * 0.9;
        }

        if (finalPrice >= 1000) {
            finalPrice -= 100;
        }

        return finalPrice;
    }
}
