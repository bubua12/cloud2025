package com.bubua12.cloud.order.controller;

import com.bubua12.cloud.model.order.OrderVO;
import com.bubua12.cloud.order.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:35
 */
@RefreshScope // 激活配置属性的自动刷新功能
@RestController
public class OrderController {

    @Resource
    private OrderService orderService;

    @Value("${order.timeout}")
    private String orderTimeout;

    @Value("${order.auto-confirm}")
    private String orderAutoConfirm;

    @GetMapping("/create")
    public OrderVO createOrder(@RequestParam("userId") Long userId,
                               @RequestParam("productId") Long productId) {
        return orderService.createOrder(productId, userId);
    }

    @GetMapping("/config")
    public String config() {
        return "order.timeout: " + orderTimeout + "\torder.auto-confirm: " + orderAutoConfirm;
    }
}
