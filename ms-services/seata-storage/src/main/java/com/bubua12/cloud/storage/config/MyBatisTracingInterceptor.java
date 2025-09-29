package com.bubua12.cloud.storage.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * MyBatis 数据库操作追踪拦截器
 * 用于追踪所有数据库操作的执行情况，并发送到OpenTelemetry后端
 *
 * @author bubua12
 * @since 2025/9/29
 */
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class MyBatisTracingInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(MyBatisTracingInterceptor.class);
    
    private final ObservationRegistry observationRegistry;
    
    @Autowired(required = false)
    private Tracer tracer;

    public MyBatisTracingInterceptor(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String sqlId = mappedStatement.getId();
        String operationType = mappedStatement.getSqlCommandType().name();
        String tableName = "storage_tbl";
        
        Span currentSpan = null;
        try {
            // 创建观测和span
            if (tracer != null) {
                currentSpan = tracer.nextSpan()
                        .name("mysql." + tableName + "." + operationType.toLowerCase())
                        .tag("db.system", "mysql")
                        .tag("db.operation", operationType)
                        .tag("db.sql.table", tableName)
                        .tag("service.name", "seata-storage")
                        .tag("db.name", "storage_db")
                        .start();
            }
            
            long startTime = System.currentTimeMillis();
            Object result = invocation.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            if (currentSpan != null) {
                currentSpan.tag("db.duration.ms", String.valueOf(duration));
            }
            
            log.debug("数据库操作完成 - SQL ID: {}, 操作类型: {}, 表名: {}, 耗时: {}ms", 
                    sqlId, operationType, tableName, duration);
            
            return result;
        } catch (Throwable e) {
            if (currentSpan != null) {
                currentSpan.tag("error", true)
                          .tag("error.message", e.getMessage());
            }
            
            log.error("数据库操作失败 - SQL ID: {}, 操作类型: {}, 错误: {}", 
                    sqlId, operationType, e.getMessage());
            throw e;
        } finally {
            if (currentSpan != null) {
                currentSpan.end();
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以设置额外的属性
    }
}