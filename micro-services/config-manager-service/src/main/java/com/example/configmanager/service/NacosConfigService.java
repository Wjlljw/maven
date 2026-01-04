package com.example.configmanager.service;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Nacos配置管理服务
 */
@Service
public class NacosConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosConfigService.class);
    private static final String GROUP = "DEFAULT_GROUP";
    private static final String FILE_EXTENSION = "yaml";

    @Autowired
    private ConfigService configService;

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    /**
     * 获取配置
     * @param serviceName 服务名称
     * @return 配置内容
     * @throws NacosException Nacos异常
     */
    public String getConfig(String serviceName) throws NacosException {
        String dataId = serviceName + "." + FILE_EXTENSION;
        return configService.getConfig(dataId, GROUP, 5000);
    }

    /**
     * 获取配置的Map形式
     * @param serviceName 服务名称
     * @return 配置Map
     * @throws NacosException Nacos异常
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getConfigMap(String serviceName) throws NacosException {
        String configContent = getConfig(serviceName);
        try {
            return yamlMapper.readValue(configContent, Map.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("解析配置失败: {}", e.getMessage());
            throw new NacosException(NacosException.SERVER_ERROR, "解析配置失败", e);
        }
    }

    /**
     * 发布配置
     * @param serviceName 服务名称
     * @param configContent 配置内容
     * @return 是否成功
     * @throws NacosException Nacos异常
     */
    public boolean publishConfig(String serviceName, String configContent) throws NacosException {
        String dataId = serviceName + "." + FILE_EXTENSION;
        boolean result = configService.publishConfig(dataId, GROUP, configContent);
        LOGGER.info("发布配置: serviceName={}, dataId={}, result={}", serviceName, dataId, result);
        return result;
    }

    /**
     * 更新配置中的特定部分
     * @param serviceName 服务名称
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 是否成功
     * @throws NacosException Nacos异常
     */
    @SuppressWarnings("unchecked")
    public boolean updateConfig(String serviceName, String configKey, Object configValue) throws NacosException {
        Map<String, Object> configMap = getConfigMap(serviceName);
        
        // 更新配置
        updateNestedMap(configMap, configKey, configValue);
        
        // 转换为YAML
        String yamlContent;
        try {
            yamlContent = yamlMapper.writeValueAsString(configMap);
        } catch (JsonProcessingException e) {
            LOGGER.error("生成YAML配置失败: {}", e.getMessage());
            throw new NacosException(NacosException.SERVER_ERROR, "生成YAML配置失败", e);
        }
        
        // 发布配置
        return publishConfig(serviceName, yamlContent);
    }

    /**
     * 递归更新嵌套Map
     * @param map 原始Map
     * @param keyPath 键路径，如 "app.discount-rates.GOLD"
     * @param value 值
     */
    @SuppressWarnings("unchecked")
    private void updateNestedMap(Map<String, Object> map, String keyPath, Object value) {
        String[] keys = keyPath.split("\\.");
        Map<String, Object> current = map;
        
        // 遍历除最后一个键之外的所有键
        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            current = (Map<String, Object>) current.computeIfAbsent(key, k -> new java.util.HashMap<>());
        }
        
        // 设置最后一个键的值
        String lastKey = keys[keys.length - 1];
        current.put(lastKey, value);
    }

    /**
     * 添加配置监听器
     * @param serviceName 服务名称
     * @param listener 监听器
     * @throws NacosException Nacos异常
     */
    public void addConfigListener(String serviceName, Listener listener) throws NacosException {
        String dataId = serviceName + "." + FILE_EXTENSION;
        configService.addListener(dataId, GROUP, listener);
    }

    /**
     * 删除配置监听器
     * @param serviceName 服务名称
     * @param listener 监听器
     * @throws NacosException Nacos异常
     */
    public void removeConfigListener(String serviceName, Listener listener) throws NacosException {
        String dataId = serviceName + "." + FILE_EXTENSION;
        configService.removeListener(dataId, GROUP, listener);
    }
}
