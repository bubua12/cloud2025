package com.bubua12.cloud.order.service.impl;

import com.bubua12.cloud.order.bean.OrderTbl;
import com.bubua12.cloud.order.feign.AccountFeignClient;
import com.bubua12.cloud.order.mapper.OrderTblMapper;
import com.bubua12.cloud.order.service.OrderService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Resource
    private OrderTblMapper orderTblMapper;

    @Resource
    private AccountFeignClient accountFeignClient;

    @Transactional
    @Override
    public OrderTbl create(String userId, String commodityCode, int orderCount) {
        // 1. 计算订单价格
        int orderMoney = calculate(orderCount);

        log.info("spend money: {}", orderMoney);

        // 2. 扣减账户余额
        accountFeignClient.debit(userId, orderMoney);

        // 3. 保存订单
        OrderTbl orderTbl = new OrderTbl();
        orderTbl.setUserId(userId);
        orderTbl.setCommodityCode(commodityCode);
        orderTbl.setCount(orderCount);
        orderTbl.setMoney(orderMoney);

        // 4. 保存订单
        orderTblMapper.insert(orderTbl);

        // Mock Exception
        int a = 100 /0 ;

        return orderTbl;
    }

    // 计算价格 这里只是模拟，假设一个商品9元钱
    private int calculate(int orderCount) {
        return 9 * orderCount;
    }
}
