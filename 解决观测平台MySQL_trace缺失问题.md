# 🎯 解决观测平台中看不到MySQL trace的问题

## 问题诊断

您的问题是：**日志中有traceId，但是在链路观测平台（如Jaeger）中看不到MySQL相关的trace信息**

这是因为：
1. 仅仅在日志中打印traceId不等于向OpenTelemetry后端发送span
2. 需要确保数据库操作能够生成独立的span并通过OTLP协议发送到后端
3. MyBatis拦截器需要正确使用ObservationRegistry来创建可被导出的spans

## 🔧 已实施的解决方案

### 1. MyBatis拦截器增强
修改了 [`MyBatisTracingInterceptor`](file://d:\workspaces\IdeaProjects\cloud2025\ms-services\seata-order\src\main\java\com\bubua12\cloud\order\config\MyBatisTracingInterceptor.java#L40-L90) 以确保：

- **创建标准化的数据库spans**：使用 `db.sql.query` 作为观测名称
- **添加OpenTelemetry标准标签**：
  - `db.system`: mysql
  - `db.operation`: INSERT/UPDATE/DELETE/SELECT
  - `db.name`: 数据库名称 
  - `db.sql.table`: 表名
  - `service.name`: 服务名称
- **双重span创建**：同时使用Observation和Tracer API确保spans被正确发送

### 2. OpenTelemetry配置增强
在application.yml中添加了：

```yaml
# OpenTelemetry 自动仪表配置
otel:
  instrumentation:
    jdbc:
      enabled: true
    mybatis:
      enabled: true
  exporter:
    otlp:
      endpoint: http://192.168.1.242:4318
  resource:
    attributes:
      service.name: seata-order
      service.version: 1.0.0
  traces:
    exporter: otlp
```

### 3. 观测配置优化
确保 [`ObservationConfig`](file://d:\workspaces\IdeaProjects\cloud2025\ms-services\seata-order\src\main\java\com\bubua12\cloud\order\config\ObservationConfig.java#L24-L35) 正确配置了：
- ObservationRegistry Bean
- ObservedAspect 支持@Observed注解

## 🚀 验证步骤

### 第1步：重启所有服务
确保新配置生效：
```bash
# 重启以下服务：
# seata-business (21000)
# seata-order (22000) 
# seata-account (20001)
# seata-storage (23000)
```

### 第2步：发起测试请求  
```bash
curl "http://localhost:21000/purchase?userId=user123&commodityCode=product001&count=2"
```

### 第3步：检查服务日志
应该看到类似这样的日志：
```log
DEBUG [seata-order,1a02343e1ca5d8d816bac02d2d324d03,NEW_SPAN_ID] c.b.c.o.c.MyBatisTracingInterceptor - 数据库操作完成 - SQL ID: com.bubua12.cloud.order.mapper.OrderTblMapper.insert, 操作类型: INSERT, 表名: order_tbl, 耗时: 15ms
```

### 第4步：在观测平台验证
在您的OpenTelemetry后端（Jaeger/Zipkin等）中应该能看到：

#### 完整的调用链路：
```
seata-business.purchase
├── http-client.seata-storage.deduct  
│   └── mysql.storage_tbl.update      // <- 新增的数据库span
├── http-client.seata-order.create
│   ├── http-client.seata-account.debit
│   │   └── mysql.account_tbl.update  // <- 新增的数据库span  
│   └── mysql.order_tbl.insert        // <- 新增的数据库span
```

#### 数据库spans包含的标签：
- `db.system`: mysql
- `db.operation`: INSERT/UPDATE
- `db.name`: order_db/account_db/storage_db
- `db.sql.table`: order_tbl/account_tbl/storage_tbl
- `service.name`: seata-order/seata-account/seata-storage
- `db.duration.ms`: 执行时间

## 🔍 如果仍然看不到数据库spans

### 检查点1：OTLP端点连通性
验证OpenTelemetry Collector是否正常运行：
```bash
curl -v http://192.168.1.242:4318/v1/traces
```
应该返回405 Method Not Allowed（说明端点存在）

### 检查点2：服务启动日志
查看服务启动时是否有错误：
```log
# 寻找这些关键词：
- "ObservationRegistry"  
- "MyBatisTracingInterceptor"
- "OTLP exporter"
- "Micrometer Tracing"
```

### 检查点3：观测注册
通过actuator端点检查观测是否正常：
```bash
curl http://localhost:22000/actuator/metrics | grep db
curl http://localhost:22000/actuator/health
```

### 检查点4：MyBatis拦截器工作状态
确认拦截器被正确注册：
```bash
curl http://localhost:22000/actuator/beans | grep MyBatisTracingInterceptor
```

## 🛠️ 故障排查

### 问题A：spans创建了但没发送
**症状**：日志显示操作完成，但观测平台没有
**解决**：检查management.otlp.tracing.endpoint配置

### 问题B：拦截器没有工作
**症状**：没有"数据库操作完成"的日志
**解决**：确认@Component注解和构造函数依赖注入

### 问题C：spans格式不符合OpenTelemetry标准
**症状**：有spans但标签不完整
**解决**：检查拦截器中的标签设置

## 📊 预期结果

配置正确后，您将在观测平台看到：

### Trace视图：
- 完整的分布式调用链路
- 每个数据库操作作为独立的span
- 清晰的父子关系和时序

### Span详情：
- span名称：`mysql.order_tbl.insert`
- 操作类型：数据库操作
- 执行时间：具体毫秒数
- 错误状态：如果有异常

### 服务拓扑：
- 微服务间的调用关系
- 数据库作为下游依赖显示
- 调用频率和错误率统计

现在，您的MySQL数据库操作应该能够在观测平台中完整可见了！🎉