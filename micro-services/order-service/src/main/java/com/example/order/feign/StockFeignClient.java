package com.example.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 库存服务Feign客户端
 */
@FeignClient(name = "stock-service")
public interface StockFeignClient {
    
    /**
     * 查询库存
     * @param itemName 商品名称
     * @return 库存数量
     */
    @GetMapping("/stock/{itemName}")
    Integer getStock(@PathVariable("itemName") String itemName);
    
    /**
     * 扣减库存
     * @param itemName 商品名称
     * @param quantity 扣减数量
     * @return 操作结果
     */
    @PostMapping("/stock/reduce")
    String reduceStock(
            @RequestParam("itemName") String itemName, 
            @RequestParam("quantity") int quantity);
}