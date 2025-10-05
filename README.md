<h1 align="center">🚀 Cloud2025 微服务架构项目</h1>

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-green)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-brightgreen)
![Spring Cloud Alibaba](https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2023.0.3.2-blue)
![Nacos](https://img.shields.io/badge/Nacos-2.4.3-blue)
![Seata](https://img.shields.io/badge/Seata-分布式事务-red)
![Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-网关-yellowgreen)
![OpenTelemetry](https://img.shields.io/badge/OpenTelemetry-链路追踪-purple)
![Docker](https://img.shields.io/badge/Docker-容器化-blue)

*基于 Spring Cloud 2025 的现代化微服务架构学习项目*

</div>

## 📋 项目简介

Cloud2025 是一个基于最新 Spring Cloud 技术栈构建的微服务架构示例项目，旨在展示现代微服务开发的最佳实践。项目集成了服务发现、配置中心、API网关、分布式事务、链路追踪等核心微服务组件。

## ✨ 核心特性

- 🌟 **最新技术栈**: 基于 Java 21 + Spring Boot 3.3.4 + Spring Cloud 2023.0.3
- 🔄 **服务发现**: 集成 Nacos 实现服务注册与发现
- 🌐 **API 网关**: Spring Cloud Gateway 统一入口与路由管理
- 💾 **分布式事务**: Seata 保证分布式系统数据一致性
- 🔍 **链路追踪**: OpenTelemetry 实现分布式链路追踪
- 📊 **服务监控**: Spring Boot Actuator + Micrometer 监控体系
- 🐳 **容器化部署**: Docker + Docker Compose 一键部署
- 🔐 **安全控制**: 自定义网关过滤器实现安全认证

## 🏗️ 系统架构
```java
TODO
```

## 📁 项目结构

```
cloud2025/
├── 📁 CI/                          # CI/CD 配置
│   ├── common/Dockerfile           # 通用 Docker 配置
│   ├── layers/                     # 分层构建配置
│   └── docker-compose.yaml         # 容器编排配置
├── 📁 ms-gateway/                  # API 网关模块
│   ├── config/                     # 网关配置
│   ├── filter/                     # 自定义过滤器
│   ├── predicate/                  # 自定义断言
│   └── GatewayMainApplication.java # 网关启动类
├── 📁 ms-model/                    # 公共模型模块
│   ├── api/CommonResult.java       # 统一响应结果
│   ├── order/OrderVO.java          # 订单视图对象
│   └── product/ProductVO.java      # 商品视图对象
└── 📁 ms-services/                 # 微服务模块
    ├── 📁 service-order/           # 订单服务
    ├── 📁 service-product/         # 商品服务
    ├── 📁 seata-account/           # 账户服务 (Seata)
    ├── 📁 seata-business/          # 业务服务 (Seata)
    ├── 📁 seata-order/             # 订单服务 (Seata)
    └── 📁 seata-storage/           # 库存服务 (Seata)
```

## 🚀 快速开始

### 📋 环境要求

- ☕ **Java**: 21+
- 🔧 **Maven**: 3.8+
- 🐳 **Docker**: 20.0+ (可选)
- 🗄️ **MySQL**: 8.0+ (用于 Seata 服务)
- 🌐 **Nacos**: 2.4.3+

### 🛠️ 本地开发

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd cloud2025
   ```

2. **启动基础设施**
   ```bash
   # 启动 Nacos (请根据实际情况配置)
   # 启动 MySQL 数据库
   # 启动 Seata Server
   ```

3. **编译项目**
   ```bash
   mvn clean compile
   ```

4. **启动服务**
   ```bash
   # 启动网关
   cd ms-gateway
   mvn spring-boot:run
   
   # 启动业务服务 (新终端)
   cd ms-services/seata-business
   mvn spring-boot:run
   
   # 启动其他服务...
   ```

### 🐳 Docker 部署

```bash
# 使用 Docker Compose 一键启动
cd CI
docker-compose up -d
```

## 🌐 服务端口

| 服务名称 | 端口 | 描述 |
|---------|------|------|
| API Gateway | 3333 | 统一网关入口 |
| Seata Business | 20001 | 业务服务 |
| Seata Account | 20002 | 账户服务 |
| Seata Order | 20003 | 订单服务 |
| Seata Storage | 20004 | 库存服务 |
| Nacos | 8848 | 注册中心/配置中心 |

## 🔗 API 接口

### 网关路由规则

| 路径 | 目标服务 | 描述 |
|------|---------|------|
| `/api/order/**` | service-order | 订单相关接口 |
| `/api/product/**` | service-product | 商品相关接口 |
| `/api/business/**` | seata-business | 分布式事务业务接口 |

### 示例接口

- **分布式事务采购**: `GET /api/business/purchase?userId=1&commodityCode=C001&count=2`

## 🔧 配置说明

### Nacos 配置

```yaml
spring:
  cloud:
    nacos:
      server-addr: ${NACOS_URL:http://127.0.0.1:8848}
      username: ${NACOS_USERNAME:nacos}
      password: ${NACOS_PASSWORD:nacos}
```

### 链路追踪配置

```yaml
management:
  tracing:
    enabled: true
    propagation:
      type: W3C

otel:
  service:
    name: bubua12-${spring.application.name}
  propagators: tracecontext,baggage
```

## 🔍 核心功能

### 1. 服务网关
- ✅ 路径重写
- ✅ 负载均衡
- ✅ 自定义过滤器
- ✅ 自定义断言
- ✅ 响应头增强

### 2. 分布式事务
- ✅ Seata AT 模式
- ✅ 多数据源事务管理
- ✅ 分布式事务回滚
- ✅ 事务日志记录

### 3. 服务治理
- ✅ 服务注册发现
- ✅ 配置中心管理
- ✅ 服务健康检查
- ✅ 负载均衡策略

### 4. 可观测性
- ✅ 分布式链路追踪
- ✅ 指标监控
- ✅ 日志聚合
- ✅ 性能分析

## 📊 监控面板

访问各服务的监控端点：
- Health Check: `http://localhost:{port}/actuator/health`
- Metrics: `http://localhost:{port}/actuator/metrics`
- Info: `http://localhost:{port}/actuator/info`

## 🛡️ 安全特性

- 🔐 网关统一认证
- 🛡️ 请求令牌验证
- 🔒 自定义安全过滤器
- 📝 访问日志记录

## 📈 性能优化

- ⚡ G1 垃圾收集器优化
- 🚀 容器化资源配置
- 📊 JVM 参数调优
- 🔄 连接池优化

## 🤝 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📝 更新日志

### v1.0.0 (2025-10-04)
- ✨ 初始版本发布
- 🚀 集成 Spring Cloud Gateway
- 💾 集成 Seata 分布式事务
- 🔍 集成 OpenTelemetry 链路追踪
- 🐳 Docker 容器化支持

## 📄 许可证

本项目采用 MIT 许可证 - 详情请查看 [LICENSE](LICENSE) 文件

## 👨‍💻 作者

**bubua12** - *项目维护者*

## 🙏 致谢

- Spring Cloud 团队
- Alibaba Spring Cloud 团队
- Seata 社区
- OpenTelemetry 社区

---

<div align="center">

**如果这个项目对你有帮助，请给它一个 ⭐ Star!**

*让我们一起构建更好的微服务架构！*

</div>