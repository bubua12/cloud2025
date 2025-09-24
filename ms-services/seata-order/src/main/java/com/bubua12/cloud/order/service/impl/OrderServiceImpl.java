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
        log.info("开始创建订单 - userId: {}, commodityCode: {}, orderCount: {}", userId, commodityCode, orderCount);
        
        // 1. 计算订单价格
        int orderMoney = calculate(orderCount);
        log.info("计算订单金额: {}", orderMoney);

        // 2. 扣减账户余额
        log.info("调用账户服务扣减余额");
        accountFeignClient.debit(userId, orderMoney);
        log.info("账户服务调用完成");

        // 3. 保存订单
        OrderTbl orderTbl = new OrderTbl();
        orderTbl.setUserId(userId);
        orderTbl.setCommodityCode(commodityCode);
        orderTbl.setCount(orderCount);
        orderTbl.setMoney(orderMoney);

        // 4. 保存订单
        log.info("保存订单到数据库");
        orderTblMapper.insert(orderTbl);
        log.info("订单创建完成 - orderId: {}", orderTbl.getId());

        // Mock Exception
//        int a = 100 /0 ;

        return orderTbl;
    }

    // 计算价格 这里只是模拟，假设一个商品9元钱
    private int calculate(int orderCount) {
        return 9 * orderCount;
    }
}