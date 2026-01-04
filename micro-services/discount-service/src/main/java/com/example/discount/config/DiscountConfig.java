package com.example.discount.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 折扣配置类
 */
@Component
@ConfigurationProperties(prefix = "app")
public class DiscountConfig implements InitializingBean {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscountConfig.class);
    
    /**
     * 折扣率配置
     */
    private Map<String, Double> discountRates = new HashMap<>();
    
    /**
     * 折扣计算配置
     */
    private Calculation calculation = new Calculation();
    
    /**
     * 初始化方法，配置加载完成后自动调用
     */
    public void afterPropertiesSet() {
        LOGGER.info("[Config Loaded] 折扣配置已从Nacos加载完成：");
        LOGGER.info("  - 折扣率配置：{}", discountRates);
        LOGGER.info("  - 最大折扣金额：{}", calculation.getMaxDiscountAmount());
        LOGGER.info("  - 最低折扣金额：{}", calculation.getMinDiscountAmount());
        LOGGER.info("  - 计算超时时间：{}", calculation.getTimeout());
    }
    
    // getter and setter
    public Map<String, Double> getDiscountRates() {
        return discountRates;
    }
    
    public void setDiscountRates(Map<String, Double> discountRates) {
        this.discountRates = discountRates;
    }
    
    public Calculation getCalculation() {
        return calculation;
    }
    
    public void setCalculation(Calculation calculation) {
        this.calculation = calculation;
    }
    
    /**
     * 计算配置内部类
     */
    public static class Calculation {
        /**
         * 最大折扣金额
         */
        private double maxDiscountAmount = 5000.0;
        
        /**
         * 最低折扣金额
         */
        private double minDiscountAmount = 0.0;
        
        /**
         * 折扣计算超时时间（毫秒）
         */
        private long timeout = 2000;
        
        // getter and setter
        public double getMaxDiscountAmount() {
            return maxDiscountAmount;
        }
        
        public void setMaxDiscountAmount(double maxDiscountAmount) {
            this.maxDiscountAmount = maxDiscountAmount;
        }
        
        public double getMinDiscountAmount() {
            return minDiscountAmount;
        }
        
        public void setMinDiscountAmount(double minDiscountAmount) {
            this.minDiscountAmount = minDiscountAmount;
        }
        
        public long getTimeout() {
            return timeout;
        }
        
        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }
    }
}