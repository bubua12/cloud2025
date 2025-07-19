package com.bubua12.cloud.order.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:36
 */
@Data
public class OrderVO {
    private Long id;
    private BigDecimal totalAmount;
    private Long userId;
    private String userNickName;
    private String userAddress;
    private List<Object> productList;
}
