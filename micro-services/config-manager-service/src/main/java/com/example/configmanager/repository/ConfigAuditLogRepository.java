package com.example.configmanager.repository;

import com.example.configmanager.model.ConfigAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 配置审计日志仓库
 */
@Repository
public interface ConfigAuditLogRepository extends JpaRepository<ConfigAuditLog, Long> {

    /**
     * 根据服务名称查询审计日志
     * @param serviceName 服务名称
     * @return 审计日志列表
     */
    List<ConfigAuditLog> findByServiceName(String serviceName);

    /**
     * 根据服务名称和配置键查询审计日志
     * @param serviceName 服务名称
     * @param configKey 配置键
     * @return 审计日志列表
     */
    List<ConfigAuditLog> findByServiceNameAndConfigKey(String serviceName, String configKey);

    /**
     * 根据操作时间范围查询审计日志
     * @param start 开始时间
     * @param end 结束时间
     * @return 审计日志列表
     */
    List<ConfigAuditLog> findByOperateTimeBetween(LocalDateTime start, LocalDateTime end);
}
