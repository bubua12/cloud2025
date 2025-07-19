package com.bubua12.cloud.product.controller;

import com.bubua12.cloud.product.entity.ProductVO;
import com.bubua12.cloud.product.service.ProduceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:20
 */
@RestController
public class ProductController {

    @Resource
    private ProduceService produceService;

    @GetMapping("/product/{id}")
    public ProductVO getProduct(@PathVariable("id") Long productId) {
        return produceService.getProductById(productId);
    }
}
