package com.bubua12.cloud.order.controller;


import com.bubua12.cloud.order.bean.OrderTbl;
import com.bubua12.cloud.order.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OrderRestController {

    @Resource
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @param userId 用户ID
     * @param commodityCode 商品编码
     * @param orderCount 购买数量
     * @return 订单ID
     */
    @GetMapping("/create")
    public String create(@RequestParam("userId") String userId,
                         @RequestParam("commodityCode") String commodityCode,
                         @RequestParam("count") int orderCount) {
        OrderTbl tbl = orderService.create(userId, commodityCode, orderCount);
        return "order create success = 订单id：[" + tbl.getId() + "]";
    }

}
