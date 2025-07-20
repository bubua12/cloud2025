package com.bubua12.cloud.order.config;

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


    // @LoadBalances 注解式负载均衡：加上该注解后、该RestTemplate自带负载均衡功能
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
