package com.bubua12.cloud.order.controller;


import com.bubua12.cloud.order.bean.OrderTbl;
import com.bubua12.cloud.order.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class OrderRestController {

    @Resource
    private OrderService orderService;

    /**
     * 创建订单
     */
    @GetMapping("/create")
    public String create(@RequestParam("userId") String userId,
                         @RequestParam("commodityCode") String commodityCode,
                         @RequestParam("count") int orderCount) {
        OrderTbl tbl = orderService.create(userId, commodityCode, orderCount);
        return "order create success = 订单id：[" + tbl.getId() + "]";
    }

}
