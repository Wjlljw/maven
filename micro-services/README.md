# 微服务架构示例

## 项目简介

这是一个将单体应用拆分为微服务架构的示例项目，包含以下微服务：

- **common-service**：公共服务模块，包含共享的实体类、异常处理类和事件类
- **discount-service**：折扣服务，负责计算商品折扣和优惠
- **stock-service**：库存服务，负责管理商品库存和扣减
- **order-service**：订单服务，负责处理订单结算和服务间调用

## 技术栈

- Spring Boot 2.7.18
- Spring Cloud 2021.0.8
- Spring Cloud Alibaba 2021.0.5.0
- Nacos 2.2.3（服务注册发现）
- Spring Cloud OpenFeign（服务间通信）
- H2 数据库（内存数据库）

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

### 4. 服务访问地址

| 服务名称 | 端口 | 访问地址 |
|----------|------|----------|
| discount-service | 8082 | http://localhost:8082/pay?price=1000&level=GOLD |
| stock-service | 8083 | http://localhost:8083/stock/MacBook |
| order-service | 8081 | http://localhost:8081/checkout?item=MacBook&price=5000&num=2&level=GOLD |

## 测试示例

### 1. 测试折扣计算

```bash
curl http://localhost:8082/pay?price=1000&level=GOLD
```

### 2. 测试库存查询

```bash
curl http://localhost:8083/stock/MacBook
```

### 3. 测试订单结算

```bash
curl http://localhost:8081/checkout?item=MacBook&price=5000&num=2&level=GOLD
```

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

## 后续优化建议

1. **添加API网关**：使用 Spring Cloud Gateway 统一管理API入口
2. **实现配置中心**：使用 Nacos 配置中心管理所有服务的配置
3. **添加服务限流熔断**：使用 Sentinel 实现服务限流和熔断
4. **添加分布式跟踪**：使用 SkyWalking 实现分布式链路跟踪
5. **使用消息队列**：引入 RabbitMQ 或 Kafka 实现异步消息处理
6. **容器化部署**：使用 Docker 和 Kubernetes 部署微服务
7. **添加监控告警**：使用 Prometheus 和 Grafana 实现监控和告警

## 注意事项

1. **服务启动顺序**：确保依赖的服务先启动
2. **端口冲突**：如果端口被占用，可以修改各服务的 `application.yml` 中的 `server.port`
3. **Nacos地址**：如果 Nacos 不是运行在 localhost:8848，需要修改各服务的 `application.yml` 中的 `spring.cloud.nacos.discovery.server-addr`
4. **数据库配置**：如果需要使用其他数据库，可以修改各服务的 `application.yml` 中的数据库配置

## 参考文档

- Spring Boot 文档：https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/
- Spring Cloud 文档：https://spring.io/projects/spring-cloud
- Spring Cloud Alibaba 文档：https://spring-cloud-alibaba-group.github.io/github-pages/greenwich/spring-cloud-alibaba.html
- Nacos 文档：https://nacos.io/zh-cn/docs/quick-start.html
- Spring Cloud OpenFeign 文档：https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/