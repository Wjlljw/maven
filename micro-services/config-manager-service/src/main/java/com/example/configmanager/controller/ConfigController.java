package com.example.configmanager.controller;

import com.alibaba.nacos.api.exception.NacosException;
import com.example.configmanager.model.ConfigAuditLog;
import com.example.configmanager.service.ConfigManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * 配置管理控制器
 */
@Controller
@RequestMapping("/config")
public class ConfigController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private ConfigManagerService configManagerService;

    /**
     * 配置管理主页
     * @return 主页视图
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 显示服务配置
     * @param serviceName 服务名称
     * @param model 模型
     * @return 配置视图
     */
    @GetMapping("/service/{serviceName}")
    public String showServiceConfig(@PathVariable String serviceName, Model model) {
        try {
                // 1. 获取原始完整配置
                Map<String, Object> allConfig = configManagerService.getServiceConfig(serviceName);
                
                // 2. 过滤只保留 app 相关的配置
                Map<String, Object> appConfig = new java.util.LinkedHashMap<>();
                if (allConfig != null && allConfig.containsKey("app")) {
                    Object appObj = allConfig.get("app");
                    if (appObj instanceof Map) {
                        // 将 app 节点下的内容直接提取到顶层展示
                        appConfig = (Map<String, Object>) appObj;
                    }
                }

                model.addAttribute("serviceName", serviceName);
                model.addAttribute("config", appConfig); // 传入前端的是过滤后的
                
                Iterable<ConfigAuditLog> auditLogs = configManagerService.getConfigAuditLogs(serviceName);
                model.addAttribute("auditLogs", auditLogs);
                
                return "service-config";
        } catch (NacosException e) {
            LOGGER.error("获取服务配置失败: {}", e.getMessage());
            model.addAttribute("error", "获取服务配置失败: " + e.getMessage());
            return "index";
        }
    }

    /**
     * 更新配置
     * @param serviceName 服务名称
     * @param configKey 配置键
     * @param configValue 配置值
     * @param redirectAttributes 重定向属性
     * @return 重定向到服务配置页面
     */
    @PostMapping("/update")
    public String updateConfig(
            @RequestParam String serviceName,
            @RequestParam String configKey,
            @RequestParam String configValue,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 转换配置值类型
            Object typedValue = convertConfigValue(configValue);
            
            // 更新配置
            boolean success = configManagerService.updateServiceConfig(serviceName, configKey, typedValue);
            
            if (success) {
                redirectAttributes.addFlashAttribute("success", "配置更新成功！");
            } else {
                redirectAttributes.addFlashAttribute("error", "配置更新失败！");
            }
        } catch (NacosException e) {
            LOGGER.error("更新配置失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "更新配置失败: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("配置值转换失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "配置值转换失败: " + e.getMessage());
        }
        
        return "redirect:/config/service/" + serviceName;
    }

    /**
     * API：获取服务配置
     * @param serviceName 服务名称
     * @return 配置Map
     * @throws NacosException Nacos异常
     */
    @GetMapping("/api/service/{serviceName}")
    @ResponseBody
    public Map<String, Object> getServiceConfigApi(@PathVariable String serviceName) throws NacosException {
        return configManagerService.getServiceConfig(serviceName);
    }

    /**
     * API：更新服务配置
     * @param serviceName 服务名称
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 结果Map
     */
    @PostMapping("/api/update")
    @ResponseBody
    public Map<String, Object> updateConfigApi(
            @RequestParam String serviceName,
            @RequestParam String configKey,
            @RequestParam String configValue) {
        
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            Object typedValue = convertConfigValue(configValue);
            boolean success = configManagerService.updateServiceConfig(serviceName, configKey, typedValue);
            
            result.put("success", success);
            result.put("message", success ? "配置更新成功" : "配置更新失败");
        } catch (Exception e) {
            LOGGER.error("更新配置失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "更新配置失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 转换配置值类型
     * @param value 字符串值
     * @return 转换后的值
     */
    private Object convertConfigValue(String value) {
        // 尝试转换为数字类型
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException e) {
            // 转换为布尔类型
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                return Boolean.parseBoolean(value);
            }
            // 保持为字符串类型
            return value;
        }
    }
}
