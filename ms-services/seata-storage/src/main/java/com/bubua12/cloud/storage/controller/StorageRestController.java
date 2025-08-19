package com.bubua12.cloud.storage.controller;


import com.bubua12.cloud.storage.service.StorageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StorageRestController {

    @Resource
    private StorageService storageService;

    /**
     * 扣减库存
     *
     * @param commodityCode 商品编码
     * @param count 扣减数量
     * @return 返回结果
     */
    @GetMapping("/deduct")
    public String deduct(@RequestParam("commodityCode") String commodityCode,
                         @RequestParam("count") Integer count) {

        storageService.deduct(commodityCode, count);
        return "storage deduct success";
    }
}
