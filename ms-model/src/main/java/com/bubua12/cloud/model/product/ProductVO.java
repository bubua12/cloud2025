package com.bubua12.cloud.model.product;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:21
 */
@Data
public class ProductVO {
    private Long id;
    private BigDecimal price;
    private String productName;
    private int quantity;
}
