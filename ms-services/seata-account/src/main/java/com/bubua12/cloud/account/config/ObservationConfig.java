package com.bubua12.cloud.account.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 观测配置类
 * 配置链路追踪相关的Bean
 *
 * @author bubua12
 * @since 2025/9/24
 */
@Configuration
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