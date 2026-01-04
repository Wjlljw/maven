package com.example.discount.service;

import com.example.common.exception.BusinessException;
import com.example.discount.config.DiscountConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 折扣服务
 */
@Service
public class DiscountService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscountService.class);
    
    @Autowired
    private DiscountConfig discountConfig;
    
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
        
        LOGGER.info("[Discount Calculation] 开始计算折扣，原始价格：{}，会员等级：{}", price, memberLevel);

        double finalPrice = price;

        // 会员等级折扣
        Double discountRate = discountConfig.getDiscountRates().get(memberLevel);
        LOGGER.info("[Discount Calculation] 从配置中心读取的折扣率：{}，会员等级：{}", discountRate, memberLevel);
        
        if (discountRate != null) {
            finalPrice = price * discountRate;
            LOGGER.info("[Discount Calculation] 应用折扣率 {}，当前价格：{}", discountRate, finalPrice);
        } else {
            // 默认使用NORMAL会员等级的折扣
            discountRate = discountConfig.getDiscountRates().get("NORMAL");
            LOGGER.info("[Discount Calculation] 使用默认折扣率：{}，会员等级：NORMAL", discountRate);
            
            if (discountRate != null) {
                finalPrice = price * discountRate;
                LOGGER.info("[Discount Calculation] 应用默认折扣率 {}，当前价格：{}", discountRate, finalPrice);
            }
        }

        // 满减优惠
        if (finalPrice >= 1000) {
            finalPrice -= 100;
            LOGGER.info("[Discount Calculation] 应用满减优惠，减100元，当前价格：{}", finalPrice);
        }

        // 应用最大和最小折扣限制
        DiscountConfig.Calculation calculation = discountConfig.getCalculation();
        double originalPrice = price;
        double discountAmount = originalPrice - finalPrice;
        
        LOGGER.info("[Discount Calculation] 配置中心读取的计算规则：");
        LOGGER.info("  - 最大折扣金额：{}", calculation.getMaxDiscountAmount());
        LOGGER.info("  - 最低折扣金额：{}", calculation.getMinDiscountAmount());
        LOGGER.info("  - 计算超时时间：{}", calculation.getTimeout());
        
        // 检查最大折扣金额
        if (discountAmount > calculation.getMaxDiscountAmount()) {
            finalPrice = originalPrice - calculation.getMaxDiscountAmount();
            LOGGER.info("[Discount Calculation] 折扣金额超过最大限制，应用最大折扣金额 {}，最终价格：{}", 
                       calculation.getMaxDiscountAmount(), finalPrice);
        }
        
        // 检查最小折扣金额
        if (discountAmount < calculation.getMinDiscountAmount()) {
            finalPrice = originalPrice - calculation.getMinDiscountAmount();
            LOGGER.info("[Discount Calculation] 折扣金额低于最小限制，应用最小折扣金额 {}，最终价格：{}", 
                       calculation.getMinDiscountAmount(), finalPrice);
        }
        
        LOGGER.info("[Discount Calculation] 折扣计算完成，最终价格：{}", finalPrice);

        return finalPrice;
    }
}