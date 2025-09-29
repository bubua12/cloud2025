package com.bubua12.cloud.order.config;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * JDBC连接级别的追踪代理
 * 用于捕获数据库连接、Statement执行等底层操作
 *
 * @author bubua12
 * @since 2025/9/29
 */
@Component
public class JdbcTracingProxy {

    private static final Logger log = LoggerFactory.getLogger(JdbcTracingProxy.class);

    @Autowired(required = false)
    private Tracer tracer;

    /**
     * 创建DataSource代理
     */
    public DataSource createDataSourceProxy(DataSource originalDataSource) {
        return (DataSource) Proxy.newProxyInstance(
                originalDataSource.getClass().getClassLoader(),
                new Class[]{DataSource.class},
                new DataSourceInvocationHandler(originalDataSource)
        );
    }

    /**
     * DataSource调用处理器
     */
    private class DataSourceInvocationHandler implements InvocationHandler {
        private final DataSource target;

        public DataSourceInvocationHandler(DataSource target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(target, args);
            
            // 代理getConnection方法返回的Connection
            if ("getConnection".equals(method.getName()) && result instanceof Connection) {
                return createConnectionProxy((Connection) result);
            }
            
            return result;
        }
    }

    /**
     * 创建Connection代理
     */
    private Connection createConnectionProxy(Connection originalConnection) {
        return (Connection) Proxy.newProxyInstance(
                originalConnection.getClass().getClassLoader(),
                new Class[]{Connection.class},
                new ConnectionInvocationHandler(originalConnection)
        );
    }

    /**
     * Connection调用处理器
     */
    private class ConnectionInvocationHandler implements InvocationHandler {
        private final Connection target;

        public ConnectionInvocationHandler(Connection target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            
            // 追踪连接相关操作
            if ("prepareStatement".equals(methodName) || "createStatement".equals(methodName)) {
                Span span = null;
                try {
                    if (tracer != null) {
                        span = tracer.nextSpan()
                                .name("jdbc.connection." + methodName)
                                .tag("db.system", "mysql")
                                .tag("db.operation.type", "connection")
                                .tag("jdbc.method", methodName)
                                .tag("service.name", "seata-order")
                                .start();
                        
                        if (args != null && args.length > 0 && args[0] instanceof String) {
                            span.tag("db.statement.preparing", (String) args[0]);
                        }
                    }
                    
                    Object result = method.invoke(target, args);
                    
                    // 代理Statement
                    if (result instanceof PreparedStatement) {
                        return createStatementProxy((PreparedStatement) result, args != null && args.length > 0 ? (String) args[0] : null);
                    } else if (result instanceof Statement) {
                        return createStatementProxy((Statement) result, null);
                    }
                    
                    return result;
                } catch (Throwable e) {
                    if (span != null) {
                        span.tag("error", true)
                            .tag("error.message", e.getMessage());
                    }
                    throw e;
                } finally {
                    if (span != null) {
                        span.end();
                    }
                }
            }
            
            // 追踪事务操作
            if ("commit".equals(methodName) || "rollback".equals(methodName) || "setAutoCommit".equals(methodName)) {
                Span span = null;
                try {
                    if (tracer != null) {
                        span = tracer.nextSpan()
                                .name("jdbc.transaction." + methodName)
                                .tag("db.system", "mysql")
                                .tag("db.operation.type", "transaction")
                                .tag("transaction.method", methodName)
                                .tag("service.name", "seata-order")
                                .start();
                        
                        if ("setAutoCommit".equals(methodName) && args != null && args.length > 0) {
                            span.tag("transaction.auto-commit", String.valueOf(args[0]));
                        }
                    }
                    
                    return method.invoke(target, args);
                } catch (Throwable e) {
                    if (span != null) {
                        span.tag("error", true)
                            .tag("error.message", e.getMessage());
                    }
                    throw e;
                } finally {
                    if (span != null) {
                        span.end();
                    }
                }
            }
            
            return method.invoke(target, args);
        }
    }

    /**
     * 创建Statement代理
     */
    private Statement createStatementProxy(Statement originalStatement, String sql) {
        return (Statement) Proxy.newProxyInstance(
                originalStatement.getClass().getClassLoader(),
                originalStatement.getClass().getInterfaces(),
                new StatementInvocationHandler(originalStatement, sql)
        );
    }

    /**
     * Statement调用处理器
     */
    private class StatementInvocationHandler implements InvocationHandler {
        private final Statement target;
        private final String originalSql;

        public StatementInvocationHandler(Statement target, String originalSql) {
            this.target = target;
            this.originalSql = originalSql;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            
            // 追踪SQL执行方法
            if (methodName.startsWith("execute") || methodName.startsWith("update") || methodName.startsWith("query")) {
                Span span = null;
                try {
                    if (tracer != null) {
                        span = tracer.nextSpan()
                                .name("jdbc.statement." + methodName)
                                .tag("db.system", "mysql")
                                .tag("db.operation.type", "statement")
                                .tag("jdbc.method", methodName)
                                .tag("service.name", "seata-order")
                                .start();
                        
                        String executingSql = originalSql;
                        if (executingSql == null && args != null && args.length > 0 && args[0] instanceof String) {
                            executingSql = (String) args[0];
                        }
                        
                        if (executingSql != null) {
                            span.tag("db.statement", executingSql.replaceAll("\\s+", " ").trim());
                        }
                    }
                    
                    long startTime = System.currentTimeMillis();
                    Object result = method.invoke(target, args);
                    long duration = System.currentTimeMillis() - startTime;
                    
                    if (span != null) {
                        span.tag("db.duration.ms", String.valueOf(duration));
                        
                        if (result instanceof Integer) {
                            span.tag("db.rows_affected", String.valueOf(result));
                        }
                    }
                    
                    return result;
                } catch (Throwable e) {
                    if (span != null) {
                        span.tag("error", true)
                            .tag("error.message", e.getMessage());
                    }
                    throw e;
                } finally {
                    if (span != null) {
                        span.end();
                    }
                }
            }
            
            return method.invoke(target, args);
        }
    }
}