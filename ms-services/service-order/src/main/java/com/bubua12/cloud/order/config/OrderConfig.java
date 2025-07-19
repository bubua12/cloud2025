package com.bubua12.cloud.order.config;

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


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
