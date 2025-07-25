package com.bubua12.cloud.order.controller;

import com.bubua12.cloud.order.properties.OrderProperties;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/25 23:26
 */
@RestController
public class OrderConfigController {

    @Resource
    private OrderProperties orderProperties;

    @GetMapping("/properties")
    public String getConfig() {
        return orderProperties.getAutoConfirm() + "\t" + orderProperties.getTimeout() + "\t" + orderProperties.getDbUrl();
    }

}
