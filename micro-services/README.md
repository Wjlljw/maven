# 微服务架构示例

## 项目简介

这是一个将单体应用拆分为微服务架构的示例项目，包含以下微服务：

- **common-service**：公共服务模块，包含共享的实体类、异常处理类和事件类
- **discount-service**：折扣服务，负责计算商品折扣和优惠
- **stock-service**：库存服务，负责管理商品库存和扣减
- **order-service**：订单服务，负责处理订单结算和服务间调用
- **config-manager-service**：配置管理服务，提供可视化界面管理各服务配置

## 技术栈

- Spring Boot 2.7.18
- Spring Cloud 2021.0.8
- Spring Cloud Alibaba 2021.0.5.0
- Nacos 2.2.3（服务注册发现与配置中心）
- Spring Cloud OpenFeign（服务间通信）
- H2 数据库（内存数据库）
- Thymeleaf（模板引擎，用于配置管理界面）
- Jackson YAML（YAML配置处理）
- Bootstrap 4（前端样式框架）
- Font Awesome（图标库）

## 运行步骤

### 1. 启动 Nacos

首先需要启动 Nacos 服务：

- 下载 Nacos：https://github.com/alibaba/nacos/releases
- 启动 Nacos（单机模式）：
  ```bash
  # Windows
  startup.cmd -m standalone
  
  # Linux/Mac
  sh startup.sh -m standalone
  ```
- 访问 Nacos 控制台：http://localhost:8848/nacos
  - 默认用户名：nacos
  - 默认密码：nacos

### 2. 编译项目

在项目根目录执行：

```bash
mvn clean install
```

### 3. 启动微服务

按照以下顺序启动微服务：

1. **启动 common-service**（可选，因为其他服务会依赖它）
2. **启动 discount-service**：
   ```bash
   cd discount-service
   mvn spring-boot:run
   ```
3. **启动 stock-service**：
   ```bash
   cd stock-service
   mvn spring-boot:run
   ```
4. **启动 order-service**：
   ```bash
   cd order-service
   mvn spring-boot:run
   ```
5. **启动 config-manager-service**：
   ```bash
   cd config-manager-service
   mvn spring-boot:run
   ```

### 4. 服务访问地址

| 服务名称 | 端口 | 访问地址 |
|----------|------|----------|
| discount-service | 8082 | http://localhost:8082/discount/pay?price=1000&level=GOLD |
| stock-service | 8083 | http://localhost:8083/stock/MacBook |
| order-service | 8081 | http://localhost:8081/order/checkout?item=MacBook&price=5000&num=2&level=GOLD |
| config-manager-service | 8085 | http://localhost:8085/config/ |

## 测试示例

### 1. 测试折扣计算

```bash
curl http://localhost:8082/discount/pay?price=1000&level=GOLD
```

### 2. 测试库存查询

```bash
curl http://localhost:8083/stock/MacBook
```

### 3. 测试订单结算

```bash
curl http://localhost:8081/checkout?item=MacBook&price=5000&num=2&level=GOLD
```

### 4. 使用配置管理服务

1. **访问配置管理界面**：
   ```
   http://localhost:8085/config/
   ```

2. **查看服务配置**：
   - 点击要管理的服务（如 `discount-service`）
   - 查看当前配置详情

3. **修改配置**：
   - 在配置更新表单中填写配置键（如 `app.discount-rates.GOLD`）
   - 填写新的配置值（如 `0.75`）
   - 点击"更新配置"按钮
   - 配置会自动更新到 Nacos 配置中心

4. **验证配置更新**：
   - 再次调用折扣服务接口，查看是否使用了新的折扣率
   ```bash
   curl http://localhost:8082/discount/pay?price=1000&level=GOLD
   ```
   - 预期返回：`750.0`（对应 75 折）

## 项目结构

```
micro-services/
├── common-service/          # 公共服务模块
│   ├── src/main/java/com/example/common/
│   │   ├── entity/         # 实体类
│   │   ├── exception/      # 异常处理
│   │   └── event/          # 事件类
│   └── pom.xml
├── discount-service/        # 折扣服务
│   ├── src/main/java/com/example/discount/
│   │   ├── controller/     # 控制器
│   │   └── service/        # 服务层
│   └── pom.xml
├── stock-service/           # 库存服务
│   ├── src/main/java/com/example/stock/
│   │   ├── controller/     # 控制器
│   │   ├── repository/     # 数据访问
│   │   └── service/        # 服务层
│   └── pom.xml
├── order-service/           # 订单服务
│   ├── src/main/java/com/example/order/
│   │   ├── controller/     # 控制器
│   │   ├── feign/          # Feign客户端
│   │   └── service/        # 服务层
│   └── pom.xml
├── config-manager-service/  # 配置管理服务
│   ├── src/main/java/com/example/configmanager/
│   │   ├── config/          # 配置类
│   │   ├── controller/     # 控制器
│   │   ├── model/           # 数据模型
│   │   ├── repository/      # 数据访问
│   │   └── service/         # 服务层
│   ├── src/main/resources/
│   │   ├── templates/       # Thymeleaf模板
│   │   ├── application.yml  # 应用配置
│   │   └── bootstrap.yml    # 启动配置
│   └── pom.xml
└── pom.xml                  # 父项目
```

## 微服务架构特点

1. **服务拆分**：按业务域拆分为独立的微服务
2. **服务注册发现**：使用 Nacos 管理服务注册和发现
3. **服务间通信**：使用 Spring Cloud OpenFeign 实现服务间调用
4. **独立部署**：每个服务可以独立编译、打包和部署
5. **独立数据库**：每个服务使用独立的 H2 数据库
6. **统一异常处理**：使用公共的异常处理机制
7. **事件驱动**：使用 Spring 事件机制实现服务间解耦
8. **配置中心**：使用 Nacos 配置中心管理所有服务的配置
9. **配置管理界面**：提供友好的可视化界面管理配置
10. **配置审计日志**：记录所有配置变更历史
11. **动态配置更新**：配置修改后自动生效，无需重启服务

## 后续优化建议

1. **添加API网关**：使用 Spring Cloud Gateway 统一管理API入口
2. **添加服务限流熔断**：使用 Sentinel 实现服务限流和熔断
3. **添加分布式跟踪**：使用 SkyWalking 实现分布式链路跟踪
4. **使用消息队列**：引入 RabbitMQ 或 Kafka 实现异步消息处理
5. **容器化部署**：使用 Docker 和 Kubernetes 部署微服务
6. **添加监控告警**：使用 Prometheus 和 Grafana 实现监控和告警
7. **增强配置管理功能**：
   - 添加配置版本管理和回滚功能
   - 实现配置变更的权限控制
   - 添加配置的审批流程
   - 实现配置的批量更新
8. **添加集成测试**：编写服务间调用的集成测试
9. **完善文档**：添加API文档和架构设计文档

## 注意事项

1. **服务启动顺序**：确保依赖的服务先启动
2. **端口冲突**：如果端口被占用，可以修改各服务的 `application.yml` 中的 `server.port`
3. **Nacos地址**：如果 Nacos 不是运行在 localhost:8848，需要修改各服务的 `application.yml` 中的 `spring.cloud.nacos.discovery.server-addr`
4. **数据库配置**：如果需要使用其他数据库，可以修改各服务的 `application.yml` 中的数据库配置
5. **配置管理服务**：
   - 配置管理服务的端口默认是 8085
   - 配置管理服务依赖 Nacos 配置中心，需要确保 Nacos 已启动
   - 配置管理服务使用 H2 内存数据库，重启后审计日志会丢失
6. **配置键格式**：在配置管理界面中，配置键需要使用点分隔的格式，如 `app.discount-rates.GOLD`

## 参考文档

- Spring Boot 文档：https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/
- Spring Cloud 文档：https://spring.io/projects/spring-cloud
- Spring Cloud Alibaba 文档：https://spring-cloud-alibaba-group.github.io/github-pages/greenwich/spring-cloud-alibaba.html
- Nacos 文档：https://nacos.io/zh-cn/docs/quick-start.html
- Spring Cloud OpenFeign 文档：https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/