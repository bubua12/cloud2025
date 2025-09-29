package com.bubua12.cloud.order.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MyBatis 数据库操作追踪拦截器
 * 用于追踪所有数据库操作的执行情况，并发送详细的SQL信息到OpenTelemetry后端
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
        String tableName = extractTableName(sqlId);
        
        // 获取完整的SQL语句
        Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String completeSql = getCompleteSql(mappedStatement.getConfiguration(), boundSql);
        
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
                        .tag("db.name", extractDatabaseName(tableName))
                        .tag("db.connection_string", "mysql://192.168.1.242:3306/" + extractDatabaseName(tableName))
                        // 服务相关标签
                        .tag("service.name", "seata-order")
                        .tag("service.version", "1.0.0")
                        // SQL相关标签
                        .tag("db.statement", boundSql.getSql().replaceAll("\\s+", " ").trim())
                        .tag("db.statement.complete", completeSql)
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
                if (result != null) {
                    if (result instanceof List) {
                        currentSpan.tag("db.rows_affected", String.valueOf(((List<?>) result).size()));
                    } else if (result instanceof Integer) {
                        currentSpan.tag("db.rows_affected", String.valueOf(result));
                    }
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
                    sqlId, operationType, tableName, duration, completeSql);
            
            return result;
        } catch (Throwable e) {
            if (currentSpan != null) {
                currentSpan.tag("error", true)
                          .tag("error.type", e.getClass().getSimpleName())
                          .tag("error.message", e.getMessage())
                          .tag("db.status", "error");
            }
            
            log.error("数据库操作失败 - SQL ID: {}, 操作类型: {}, SQL: {}, 错误: {}", 
                    sqlId, operationType, completeSql, e.getMessage());
            throw e;
        } finally {
            if (currentSpan != null) {
                currentSpan.tag("db.status", "completed");
                currentSpan.end();
            }
        }
    }
    
    /**
     * 获取完整的SQL语句（包含参数）
     */
    private String getCompleteSql(Configuration configuration, BoundSql boundSql) {
        try {
            Object parameterObject = boundSql.getParameterObject();
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
            
            if (parameterMappings.size() > 0 && parameterObject != null) {
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    for (ParameterMapping parameterMapping : parameterMappings) {
                        String propertyName = parameterMapping.getProperty();
                        if (metaObject.hasGetter(propertyName)) {
                            Object obj = metaObject.getValue(propertyName);
                            sql = sql.replaceFirst("\\?", getParameterValue(obj));
                        } else if (boundSql.hasAdditionalParameter(propertyName)) {
                            Object obj = boundSql.getAdditionalParameter(propertyName);
                            sql = sql.replaceFirst("\\?", getParameterValue(obj));
                        }
                    }
                }
            }
            return sql;
        } catch (Exception e) {
            log.warn("获取完整SQL失败: {}", e.getMessage());
            return boundSql.getSql();
        }
    }
    
    /**
     * 获取参数的字符串表示
     */
    private String getParameterValue(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof String) {
            return "'" + obj + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            return "'" + formatter.format(obj) + "'";
        } else {
            return obj.toString();
        }
    }

    private String extractTableName(String sqlId) {
        if (sqlId.contains("OrderTbl")) {
            return "order_tbl";
        } else if (sqlId.contains("Account")) {
            return "account_tbl";
        } else if (sqlId.contains("Storage")) {
            return "storage_tbl";
        }
        return "unknown_table";
    }
    
    private String extractDatabaseName(String tableName) {
        if (tableName.contains("order")) {
            return "order_db";
        } else if (tableName.contains("account")) {
            return "account_db";
        } else if (tableName.contains("storage")) {
            return "storage_db";
        }
        return "unknown_db";
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