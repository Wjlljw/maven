package com.example.service;

import com.example.event.OrderSettledEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SettlementService {
    
    // 折扣服务：用于计算会员折扣和满减优惠
    @Autowired private DiscountService discountService;
    
    // 事件发布器：用于发布订单结算事件，触发后续库存扣减操作
    @Autowired private ApplicationEventPublisher eventPublisher;

    /**
     * 处理订单结算
     * @param itemName 商品名称
     * @param unitPrice 单价
     * @param quantity 购买数量
     * @param memberLevel 会员等级（GOLD、SILVER等）
     * @return 计算后的最终支付价格
     */
    public double processOrder(String itemName, double unitPrice, int quantity, String memberLevel) {
        // 步骤1：计算商品总价（单价 × 数量）
        double totalPrice = unitPrice * quantity;
        
        // 步骤2：根据会员等级计算最终价格（包含折扣和满减）
        double finalPrice = discountService.calculateFinalPrice(totalPrice, memberLevel);
        
        // 步骤3：打印结算完成日志
        System.out.println("订单结算完成，准备触发库存扣减...");
        
        // 步骤4：发布订单结算事件，通知库存服务扣减库存
        // 使用 Spring 事件机制解耦业务逻辑
        eventPublisher.publishEvent(new OrderSettledEvent(itemName, quantity));

        // 返回最终支付金额
        return finalPrice;
    }
}
