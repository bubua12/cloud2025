package com.bubua12.cloud.product.service.impl;

import com.bubua12.cloud.model.product.ProductVO;
import com.bubua12.cloud.product.service.ProduceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:23
 */
@Service
public class ProduceServiceImpl implements ProduceService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public ProductVO getProductById(Long productId) {
        log.info("Product的Service Impl方法");
        ProductVO productVO = new ProductVO();

        productVO.setId(productId);
        productVO.setProductName("Apple iPhone " + productId);
        productVO.setPrice(new BigDecimal(8999));
        productVO.setQuantity(2);

        // 模拟调用超时、重试
//        try { TimeUnit.SECONDS.sleep(100); } catch (InterruptedException e) { throw new RuntimeException(e); }

        return productVO;
    }
}
