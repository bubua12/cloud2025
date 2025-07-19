package com.bubua12.cloud.order.service.impl;

import com.bubua12.cloud.model.order.OrderVO;
import com.bubua12.cloud.model.product.ProductVO;
import com.bubua12.cloud.order.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/19 9:38
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private RestTemplate restTemplate;


    @Override
    public OrderVO createOrder(Long productId, Long userId) {
        ProductVO productVO = getProductByProductIdRPC(productId);

        OrderVO orderVO = new OrderVO();

        orderVO.setId(1L);
        orderVO.setUserId(userId);
        orderVO.setTotalAmount(productVO.getPrice().multiply(new BigDecimal(productVO.getQuantity())));
        orderVO.setUserNickName("张利");
        orderVO.setUserAddress("江宁区禄口街道123号");
        orderVO.setUserId(userId);
        orderVO.setProductList(List.of(productVO));

        return orderVO;
    }

    private ProductVO getProductByProductIdRPC(Long productId) {
        // 1、获取到商品服务所在的所有机器 IP:Port
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");

        // fixme @负载均衡 直接获取第一个 没有负载均衡
        ServiceInstance instance = instances.getFirst();
        // 远程调用 URL
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/product/" + productId;
        log.info("远程请求路径: {}", url);

        // 2、给远程发送请求
        ProductVO productVO = restTemplate.getForObject(url, ProductVO.class);
        log.debug("远程响应结构体: {}", productVO);

        return productVO;
    }

}
