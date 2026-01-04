package com.example.discount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 折扣服务主应用类
 */
@SpringBootApplication
@EnableDiscoveryClient  // 启用服务注册发现
public class DiscountApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscountApplication.class, args);
    }
}