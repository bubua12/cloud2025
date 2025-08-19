package com.bubua12.cloud.business.controller;

import com.bubua12.cloud.business.service.BusinessService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseRestController {

    @Resource
    private BusinessService businessService;

    /**
     * 采购：哪个用户要买哪个商品要买几件
     */
    @GetMapping("/purchase")
    public String purchase(@RequestParam("userId") String userId,
                           @RequestParam("commodityCode") String commodityCode,
                           @RequestParam("count") int orderCount) {
        businessService.purchase(userId, commodityCode, orderCount);
        return "business purchase success";
    }
}
