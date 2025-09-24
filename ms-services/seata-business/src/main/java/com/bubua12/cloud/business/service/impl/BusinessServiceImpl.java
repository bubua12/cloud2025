package com.bubua12.cloud.business.service.impl;

import com.bubua12.cloud.business.feign.OrderFeignClient;
import com.bubua12.cloud.business.feign.StorageFeignClient;
import com.bubua12.cloud.business.service.BusinessService;
import jakarta.annotation.Resource;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
public class BusinessServiceImpl implements BusinessService {
    
    private static final Logger log = LoggerFactory.getLogger(BusinessServiceImpl.class);

    @Resource
    private StorageFeignClient storageFeignClient;

    @Resource
    private OrderFeignClient orderFeignClient;

    @GlobalTransactional
    @Override
    public void purchase(String userId, String commodityCode, int orderCount) {
        log.info("开始处理购买请求 - userId: {}, commodityCode: {}, orderCount: {}", userId, commodityCode, orderCount);
        
        // 1. 扣减库存
        log.info("调用库存服务扣减库存");
        storageFeignClient.deduct(commodityCode, orderCount);
        log.info("库存服务调用完成");
        
        // 2. 创建订单
        log.info("调用订单服务创建订单");
        orderFeignClient.create(userId, commodityCode, orderCount);
        log.info("订单服务调用完成");
        
        log.info("购买请求处理完成");
    }
}