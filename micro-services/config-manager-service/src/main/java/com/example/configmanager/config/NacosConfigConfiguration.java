package com.example.configmanager.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Nacos配置类，用于手动配置ConfigService Bean
 */
@Configuration
public class NacosConfigConfiguration {

    /**
     * 手动配置ConfigService Bean
     * @return ConfigService实例
     * @throws NacosException Nacos异常
     */
    @Bean
    public ConfigService configService() throws NacosException {
        Properties properties = new Properties();
        // 直接配置Nacos服务器地址，避免依赖Spring Cloud配置加载
        properties.put("serverAddr", "localhost:8848");
        properties.put("group", "DEFAULT_GROUP");
        
        return NacosFactory.createConfigService(properties);
    }
}
