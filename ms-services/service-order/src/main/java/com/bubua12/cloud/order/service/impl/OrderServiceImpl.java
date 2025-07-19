package com.bubua12.cloud.order.service.impl;

import com.bubua12.cloud.order.entity.OrderVO;
import com.bubua12.cloud.order.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:38
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public OrderVO createOrder(Long productId, Long userId) {
        OrderVO orderVO = new OrderVO();

        orderVO.setId(1L);
        orderVO.setUserId(userId);
        // TODO 总金额
        orderVO.setTotalAmount(new BigDecimal(8999));
        orderVO.setUserNickName("张利");
        orderVO.setUserAddress("江宁区禄口街道123号");
        orderVO.setUserId(userId);
        // TODO 远程查询商品列表
        orderVO.setProductList(null);

        return orderVO;
    }
}
