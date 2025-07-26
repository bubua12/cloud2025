package com.bubua12.cloud.order.config;

import feign.Logger;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:55
 */
@Configuration
public class OrderConfig {

    /**
     * 重试器、放到容器里自动识别
     */
//    @Bean
//    public Retryer retryer() {
//        // 默认的 this(100L, TimeUnit.SECONDS.toMillis(1L), 5); 也可以自行指定参数
//        return new Retryer.Default();
//    }


    // @LoadBalances 注解式负载均衡：加上该注解后、该RestTemplate自带负载均衡功能
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Feign日志全记录组件
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
