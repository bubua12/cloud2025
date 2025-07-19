package com.bubua12.cloud.order.service;


import com.bubua12.cloud.model.order.OrderVO;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:37
 */
public interface OrderService {
    OrderVO createOrder(Long productId, Long userId);
}
