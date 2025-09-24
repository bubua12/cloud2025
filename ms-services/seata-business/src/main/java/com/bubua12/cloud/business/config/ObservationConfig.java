package com.bubua12.cloud.business.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 观测配置类
 * 配置链路追踪相关的Bean
 *
 * @author bubua12
 * @since 2025/9/24
 */
@Configuration
@EnableFeignClients(basePackages = "com.bubua12.cloud.business.feign")
public class ObservationConfig {
    
    /**
     * 确保ObservationRegistry正确配置
     * 这是Micrometer Tracing的核心组件
     */
    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }
}