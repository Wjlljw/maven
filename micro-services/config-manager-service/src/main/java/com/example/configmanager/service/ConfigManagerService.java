package com.example.configmanager.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.example.configmanager.model.ConfigAuditLog;
import com.example.configmanager.repository.ConfigAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 配置管理服务
 */
@Service
public class ConfigManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManagerService.class);
    private static final String DEFAULT_OPERATOR = "admin";

    @Autowired
    private NacosConfigService nacosConfigService;

    @Autowired
    private ConfigAuditLogRepository auditLogRepository;

    /**
     * 获取服务配置
     * @param serviceName 服务名称
     * @return 配置Map
     * @throws NacosException Nacos异常
     */
    public Map<String, Object> getServiceConfig(String serviceName) throws NacosException {
        return nacosConfigService.getConfigMap(serviceName);
    }

    /**
     * 更新服务配置
     * @param serviceName 服务名称
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 是否成功
     * @throws NacosException Nacos异常
     */
    public boolean updateServiceConfig(String serviceName, String configKey, Object configValue) throws NacosException {
        // 获取旧值
        Map<String, Object> oldConfig = nacosConfigService.getConfigMap(serviceName);
        Object oldValue = getNestedValue(oldConfig, configKey);

        // 更新配置
        boolean result = nacosConfigService.updateConfig(serviceName, configKey, configValue);

        // 记录审计日志
        if (result) {
            ConfigAuditLog log = new ConfigAuditLog();
            log.setServiceName(serviceName);
            log.setConfigKey(configKey);
            log.setOldValue(String.valueOf(oldValue));
            log.setNewValue(String.valueOf(configValue));
            log.setOperator(DEFAULT_OPERATOR);
            log.setOperateTime(LocalDateTime.now());
            auditLogRepository.save(log);
            LOGGER.info("配置更新成功，已记录审计日志: serviceName={}, configKey={}, oldValue={}, newValue={}",
                    serviceName, configKey, oldValue, configValue);
        }

        return result;
    }

    /**
     * 获取嵌套Map中的值
     * @param map 原始Map
     * @param keyPath 键路径，如 "app.discount-rates.GOLD"
     * @return 值
     */
    @SuppressWarnings("unchecked")
    private Object getNestedValue(Map<String, Object> map, String keyPath) {
        String[] keys = keyPath.split("\\.");
        Map<String, Object> current = map;
        
        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            Object value = current.get(key);
            if (value == null) {
                return null;
            }
            current = (Map<String, Object>) value;
        }
        
        return current.get(keys[keys.length - 1]);
    }

    /**
     * 获取配置审计日志
     * @param serviceName 服务名称
     * @return 审计日志列表
     */
    public Iterable<ConfigAuditLog> getConfigAuditLogs(String serviceName) {
        return auditLogRepository.findByServiceName(serviceName);
    }
}
