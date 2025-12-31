package com.example.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 折扣服务Feign客户端
 */
@FeignClient(name = "discount-service")
public interface DiscountFeignClient {
    
    /**
     * 计算最终价格
     * @param price 原始价格
     * @param level 会员等级
     * @return 计算结果
     */
    @GetMapping("/pay")
    String getFinalPrice(@RequestParam double price, @RequestParam String level);
}