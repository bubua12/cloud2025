package com.bubua12.cloud.order.service.impl;

import com.bubua12.cloud.order.bean.OrderTbl;
import com.bubua12.cloud.order.feign.AccountFeignClient;
import com.bubua12.cloud.order.mapper.OrderTblMapper;
import com.bubua12.cloud.order.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderTblMapper orderTblMapper;

    @Resource
    private AccountFeignClient accountFeignClient;

    @Override
    public OrderTbl create(String userId, String commodityCode, int orderCount) {
        // 1. 计算订单价格
        int orderMoney = calculate(orderCount);
        // 2. 扣减账户余额
        // fixme

        // 3. 保存订单
        OrderTbl orderTbl = new OrderTbl();
        orderTbl.setUserId(userId);
        orderTbl.setCommodityCode(commodityCode);
        orderTbl.setCount(orderCount);
        orderTbl.setMoney(orderMoney);

        // 4. 保存订单
        orderTblMapper.insert(orderTbl);

        return orderTbl;
    }

    // 计算价格 这里只是模拟，假设一个商品9元钱
    private int calculate(int orderCount) {
        return 9 * orderCount;
    }
}
