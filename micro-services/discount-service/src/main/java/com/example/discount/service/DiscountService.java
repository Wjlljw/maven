package com.example.discount.service;

import com.example.common.exception.BusinessException;
import org.springframework.stereotype.Service;

/**
 * 折扣服务
 */
@Service
public class DiscountService {
    
    /**
     * 计算最终价格
     * @param price 原始价格
     * @param memberLevel 会员等级
     * @return 最终价格
     */
    public double calculateFinalPrice(double price, String memberLevel) {
        if (price < 0) {
            throw new BusinessException("金额不能为负数");
        }

        double finalPrice = price;

        // 会员等级折扣
        if ("GOLD".equals(memberLevel)) {
            finalPrice = price * 0.8;
        } else if ("SILVER".equals(memberLevel)) {
            finalPrice = price * 0.9;
        }

        // 满减优惠
        if (finalPrice >= 1000) {
            finalPrice -= 100;
        }

        return finalPrice;
    }
}