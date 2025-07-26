package com.bubua12.cloud.order;

import com.bubua12.cloud.order.feign.WeatherFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/26 16:18
 */
@SpringBootTest
public class WeatherTests {
    @Autowired
    WeatherFeignClient weatherClient;

    @Test
    void contextLoads() {
        String weatherInfo = weatherClient.getWeather("APPCODE 5d215b0c7a5f401ea216718799256b81",
                "1055",
                "50b53ff8dd7d9fa320d3d3ca32cf8ed1");
        System.out.println(weatherInfo);
    }
}
