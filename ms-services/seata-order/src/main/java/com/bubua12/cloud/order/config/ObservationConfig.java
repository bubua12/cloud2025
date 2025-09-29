package com.bubua12.cloud.order.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 观测配置类
 * 配置链路追踪相关的Bean，包括数据库操作追踪
 *
 * @author bubua12
 * @since 2025/9/24
 */
@Configuration
@EnableFeignClients(basePackages = "com.bubua12.cloud.order.feign")
public class ObservationConfig {
    
    /**
     * 确保ObservationRegistry正确配置
     * 这是Micrometer Tracing的核心组件
     */
    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }
    
    /**
     * 启用@Observed注解支持
     * 用于Service层方法的链路追踪
     */
    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}