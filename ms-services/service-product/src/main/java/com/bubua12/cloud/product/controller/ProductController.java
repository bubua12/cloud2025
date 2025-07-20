package com.bubua12.cloud.product.controller;

import com.bubua12.cloud.model.product.ProductVO;
import com.bubua12.cloud.product.service.ProduceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class ProductController {
    @Value("${server.port}")
    private String serverPort;

    @Resource
    private ProduceService produceService;

    @GetMapping("/product/{id}")
    public ProductVO getProduct(@PathVariable("id") Long productId) {
        log.info("server.port: {}", serverPort);
        return produceService.getProductById(productId);
    }
}
