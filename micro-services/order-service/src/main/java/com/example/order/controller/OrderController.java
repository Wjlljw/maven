package com.example.order.controller;

import com.example.order.feign.StockFeignClient;
import com.example.order.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired private SettlementService settlementService;
    @Autowired private StockFeignClient stockFeignClient;

    /**
     * 订单结算
     * @param item 商品名称
     * @param price 单价
     * @param num 数量
     * @param level 会员等级
     * @return 结算结果
     */
    @GetMapping("/checkout")
    public ResponseEntity<String> checkout(
            @RequestParam String item,
            @RequestParam double price,
            @RequestParam int num,
            @RequestParam String level) {
        // 复用create方法的逻辑
        return create(item, price, num, level);
    }
    
    /**
     * 创建订单
     * @param itemName 商品名称
     * @param quantity 数量
     * @return 订单结果
     */
    @GetMapping("/create")
    public ResponseEntity<String> create(
            @RequestParam String itemName,
            @RequestParam int quantity) {
        // 默认参数
        double defaultPrice = 1000.0; // 默认价格
        String defaultLevel = "SILVER"; // 默认会员等级
        
        return create(itemName, defaultPrice, quantity, defaultLevel);
    }
    
    /**
     * 创建订单（内部方法）
     * @param item 商品名称
     * @param price 单价
     * @param num 数量
     * @param level 会员等级
     * @return 结算结果
     */
    private ResponseEntity<String> create(
            String item,
            double price,
            int num,
            String level) {
        try {
            // 调用库存服务检查库存
            Integer currentStock = stockFeignClient.getStock(item);
            if (currentStock == null) {
                return new ResponseEntity<>("下单失败：商品不存在", HttpStatus.BAD_REQUEST);
            }
            
            if (currentStock < num) {
                return new ResponseEntity<>("下单失败：库存不足，当前剩余：" + currentStock, HttpStatus.BAD_REQUEST);
            }

            // 处理订单结算
            double finalPrice = settlementService.processOrder(item, price, num, level);
            
            // 再次查询库存，获取最新库存数量
            Integer updatedStock = stockFeignClient.getStock(item);
            
            String result = String.format("购买成功！商品：%s, 数量：%d, 实付：%.2f, 剩余库存：%d", 
                    item, num, finalPrice, updatedStock);
            
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("下单失败：" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}