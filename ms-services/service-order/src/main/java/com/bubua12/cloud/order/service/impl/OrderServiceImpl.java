package com.bubua12.cloud.order.service.impl;

import com.bubua12.cloud.model.order.OrderVO;
import com.bubua12.cloud.model.product.ProductVO;
import com.bubua12.cloud.order.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
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

    @Resource
    private LoadBalancerClient loadBalancerClient;

    @Override
    public OrderVO createOrder(Long productId, Long userId) {
//        ProductVO productVO = getProductByProductIdRPC(productId);
//        ProductVO productVO = getProductByProductIdRPCLB(productId);
        ProductVO productVO = getProductByProductIdRPCAnnoLB(productId);

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

    private ProductVO getProductByProductIdRPCLB(Long productId) {
        // 负载均衡的获取服务实例、而不是获取第一个
        ServiceInstance choose = loadBalancerClient.choose("service-product");

        // 远程调用 URL
        String url = "http://" + choose.getHost() + ":" + choose.getPort() + "/product/" + productId;
        log.info("远程请求路径: {}", url);

        // 2、给远程发送请求
        ProductVO productVO = restTemplate.getForObject(url, ProductVO.class);
        log.debug("远程响应结构体: {}", productVO);

        return productVO;
    }

    /**
     * 远程请求路径: <a href="http://192.168.31.74:9002/product/100">http://192.168.31.74:9002/product/100</a>
     * <br/>
     * 更新后的远程请求路径: <a href="http://service-product/product/100">http://service-product/product/100</a>
     * <br/>
     * 思考题：注册中心宕机了，远程调用还能成功吗？
     *
     */
    private ProductVO getProductByProductIdRPCAnnoLB(Long productId) {
        // 给服务名发送请求、会被动态替换
        String url = "http://" + "service-product" + "/product/" + productId;
        log.info("远程请求路径: {}", url);

        // 2、给远程发送请求
        ProductVO productVO = restTemplate.getForObject(url, ProductVO.class);
        log.debug("远程响应结构体: {}", productVO);

        return productVO;
    }

}
