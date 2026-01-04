package com.example.configmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 配置管理服务主应用类
 */
@SpringBootApplication
@EnableDiscoveryClient  // 启用服务注册发现
public class ConfigManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigManagerApplication.class, args);
    }

}
