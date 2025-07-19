package com.bubua12.cloud.product.service;

import com.bubua12.cloud.product.entity.ProductVO;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:22
 */
public interface ProduceService {
    ProductVO getProductById(Long productId);
}
