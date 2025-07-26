package com.bubua12.cloud.order.feign;

import com.bubua12.cloud.model.product.ProductVO;
import org.springframework.stereotype.Component;

/**
 * ProductFeignClient兜底回调方法
 *
 * @author bubua12
 * @since 2025/7/27 4:27
 */
@Component
public class ProductFeignClientFallback implements ProductFeignClient {

    @Override
    public ProductVO getProductById(Long id) {
        System.out.println("兜底回调... ...");

        ProductVO defaultVO = new ProductVO();
        defaultVO.setId(id);
        defaultVO.setProductName("UNKNOW");
        defaultVO.setQuantity(0);

        return defaultVO;
    }
}
