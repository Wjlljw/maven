package com.example.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 库存服务主应用类
 */
@SpringBootApplication(scanBasePackages = {"com.example"}) // 扫描所有com.example包
@EntityScan(basePackages = {"com.example.common.entity"}) // 扫描公共实体包
@EnableJpaRepositories(basePackages = {"com.example.stock.repository"}) // 扫描本服务的Repository
public class StockApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }
}