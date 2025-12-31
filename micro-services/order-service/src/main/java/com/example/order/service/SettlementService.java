package com.example.order.service;

import com.example.common.event.OrderSettledEvent;
import com.example.order.feign.DiscountFeignClient;
import com.example.order.feign.StockFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 结算服务
 */
@Service
public class SettlementService {
    
    // 折扣服务Feign客户端
    @Autowired private DiscountFeignClient discountFeignClient;
    
    // 库存服务Feign客户端
    @Autowired private StockFeignClient stockFeignClient;
    
    // 事件发布器
    @Autowired private ApplicationEventPublisher eventPublisher;

    /**
     * 处理订单结算
     * @param itemName 商品名称
     * @param unitPrice 单价
     * @param quantity 购买数量
     * @param memberLevel 会员等级
     * @return 计算后的最终支付价格
     */
    public double processOrder(String itemName, double unitPrice, int quantity, String memberLevel) {
        // 步骤1：计算商品总价（单价 × 数量）
        double totalPrice = unitPrice * quantity;
        
        // 步骤2：调用折扣服务计算最终价格
        // 注意：这里简化处理，直接解析返回结果
        String discountResult = discountFeignClient.getFinalPrice(totalPrice, memberLevel);
        // 从返回结果中提取最终价格
        double finalPrice = parseFinalPrice(discountResult);
        
        // 步骤3：调用库存服务扣减库存
        stockFeignClient.reduceStock(itemName, quantity);
        
        // 步骤4：打印结算完成日志
        System.out.println("订单结算完成，已触发库存扣减...");
        
        // 步骤5：发布订单结算事件（可选，用于异步处理）
        eventPublisher.publishEvent(new OrderSettledEvent(itemName, quantity));

        // 返回最终支付金额
        return finalPrice;
    }
    
    /**
     * 从折扣服务返回结果中解析最终价格
     * @param discountResult 折扣服务返回结果
     * @return 最终价格
     */
    private double parseFinalPrice(String discountResult) {
        // 简化解析，实际项目中应使用更可靠的方式
        try {
            String[] parts = discountResult.split("折后价：");
            if (parts.length > 1) {
                return Double.parseDouble(parts[1]);
            }
            throw new RuntimeException("解析折扣结果失败：" + discountResult);
        } catch (Exception e) {
            throw new RuntimeException("解析折扣结果失败：" + e.getMessage());
        }
    }
}