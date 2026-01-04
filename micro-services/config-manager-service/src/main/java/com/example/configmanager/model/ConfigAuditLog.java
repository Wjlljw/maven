package com.example.configmanager.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 配置审计日志实体
 */
@Entity
@Table(name = "config_audit_log")
public class ConfigAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 服务名称
     */
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    /**
     * 配置键
     */
    @Column(name = "config_key", nullable = false)
    private String configKey;

    /**
     * 旧值
     */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    /**
     * 新值
     */
    @Column(name = "new_value", columnDefinition = "TEXT", nullable = false)
    private String newValue;

    /**
     * 操作人
     */
    @Column(name = "operator", nullable = false)
    private String operator;

    /**
     * 操作时间
     */
    @Column(name = "operate_time", nullable = false)
    private LocalDateTime operateTime;

    // getter and setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public LocalDateTime getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(LocalDateTime operateTime) {
        this.operateTime = operateTime;
    }
}
