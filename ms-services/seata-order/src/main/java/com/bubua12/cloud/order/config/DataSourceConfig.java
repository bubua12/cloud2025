package com.bubua12.cloud.order.config;

import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 数据源配置类，启用数据库链路追踪
 *
 * @author bubua12
 * @since 2025/9/29
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties, ObservationRegistry observationRegistry, JdbcTracingProxy jdbcTracingProxy) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        
        // 配置Hikari连接池监控
        dataSource.setPoolName("order-db-pool");
        dataSource.setRegisterMbeans(true);
        dataSource.setMaximumPoolSize(20);
        dataSource.setMinimumIdle(5);
        
        // 使用JDBC追踪代理包装数据源
        return jdbcTracingProxy.createDataSourceProxy(dataSource);
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(DataSource dataSource, ObservationRegistry observationRegistry) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}