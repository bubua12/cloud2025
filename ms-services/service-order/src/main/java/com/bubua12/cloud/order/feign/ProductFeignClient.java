package com.bubua12.cloud.order.feign;

import com.bubua12.cloud.model.product.ProductVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/26 15:37
 */
@FeignClient(value = "service-product") // Feign客户端，发送远程请求的客户端
public interface ProductFeignClient {

    /**
     * SpringMVC注解的两套使用逻辑
     * 1、标注在 Controller上，是接收这样的请求
     * 2、标注在 FeignClient上，是发送这样的请求
     */
    @GetMapping("/product/{id}")
    ProductVO getProductById(@PathVariable("id") Long id);
}
