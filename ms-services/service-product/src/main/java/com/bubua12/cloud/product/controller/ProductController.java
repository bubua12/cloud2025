package com.bubua12.cloud.product.controller;

import com.bubua12.cloud.model.product.ProductVO;
import com.bubua12.cloud.product.service.ProduceService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:20
 */
@RequestMapping("/api/product")
@RestController
public class ProductController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private ProduceService produceService;

    /**
     * fixme 这里UUID只生成一次？
     */
    @GetMapping("/product/{id}")
    public ProductVO getProduct(@PathVariable("id") Long productId,
                                HttpServletRequest request) {
        System.out.println("hello order, this is product controller, header: " + request.getHeader("X-Token"));
        log.info("server.port: {}", serverPort);
        return produceService.getProductById(productId);
    }
}
