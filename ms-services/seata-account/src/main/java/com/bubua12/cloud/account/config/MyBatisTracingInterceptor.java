package com.bubua12.cloud.account.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

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
        String tableName = "account_tbl";
        
        // 获取完整的SQL语句
        Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String originalSql = boundSql.getSql().replaceAll("\\s+", " ").trim();
        
        Span currentSpan = null;
        try {
            // 创建更详细的span
            if (tracer != null) {
                currentSpan = tracer.nextSpan()
                        .name("mysql." + tableName + "." + operationType.toLowerCase())
                        // 数据库相关标签
                        .tag("db.system", "mysql")
                        .tag("db.operation", operationType)
                        .tag("db.sql.table", tableName)
                        .tag("db.name", "account_db")
                        .tag("db.connection_string", "mysql://192.168.1.242:3306/account_db")
                        // 服务相关标签
                        .tag("service.name", "seata-account")
                        .tag("service.version", "1.0.0")
                        // SQL相关标签
                        .tag("db.statement", originalSql)
                        .tag("db.mapper.method", sqlId)
                        // 参数信息
                        .tag("db.parameter.count", String.valueOf(boundSql.getParameterMappings().size()))
                        .start();
            }
            
            long startTime = System.currentTimeMillis();
            Object result = invocation.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            if (currentSpan != null) {
                currentSpan.tag("db.duration.ms", String.valueOf(duration));
                
                // 添加结果相关信息
                if (result instanceof Integer) {
                    currentSpan.tag("db.rows_affected", String.valueOf(result));
                }
                
                // 性能分级
                if (duration > 1000) {
                    currentSpan.tag("db.performance", "slow");
                } else if (duration > 500) {
                    currentSpan.tag("db.performance", "medium");
                } else {
                    currentSpan.tag("db.performance", "fast");
                }
            }
            
            log.debug("数据库操作完成 - SQL ID: {}, 操作类型: {}, 表名: {}, 耗时: {}ms, SQL: {}", 
                    sqlId, operationType, tableName, duration, originalSql);
            
            return result;
        } catch (Throwable e) {
            if (currentSpan != null) {
                currentSpan.tag("error", true)
                          .tag("error.type", e.getClass().getSimpleName())
                          .tag("error.message", e.getMessage())
                          .tag("db.status", "error");
            }
            
            log.error("数据库操作失败 - SQL ID: {}, 操作类型: {}, SQL: {}, 错误: {}", 
                    sqlId, operationType, originalSql, e.getMessage());
            throw e;
        } finally {
            if (currentSpan != null) {
                currentSpan.tag("db.status", "completed");
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
    }
}