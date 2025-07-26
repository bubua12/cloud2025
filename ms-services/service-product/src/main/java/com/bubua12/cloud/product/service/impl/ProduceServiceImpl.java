package com.bubua12.cloud.product.service.impl;

import com.bubua12.cloud.model.product.ProductVO;
import com.bubua12.cloud.product.service.ProduceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:23
 */
@Service
public class ProduceServiceImpl implements ProduceService {

    @Override
    public ProductVO getProductById(Long productId) {
        ProductVO productVO = new ProductVO();

        productVO.setId(productId);
        productVO.setProductName("Apple iPhone " + productId);
        productVO.setPrice(new BigDecimal(8999));
        productVO.setQuantity(2);

        try { TimeUnit.SECONDS.sleep(100); } catch (InterruptedException e) { throw new RuntimeException(e); }

        return productVO;
    }
}
