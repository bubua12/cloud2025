package com.bubua12.cloud.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

/**
 *
 *
 * @author bubua12
 * @since 2025/7/21 0:01
 */
@SpringBootTest
public class LBTests {
    @Autowired
    LoadBalancerClient loadBalancer;

    @Test
    public void test() {
        ServiceInstance choose = loadBalancer.choose("service-product");
        System.out.println("choose: " + choose.getHost() + ":" + choose.getPort());

        choose = loadBalancer.choose("service-product");
        System.out.println("choose: " + choose.getHost() + ":" + choose.getPort());

        choose = loadBalancer.choose("service-product");
        System.out.println("choose: " + choose.getHost() + ":" + choose.getPort());

        choose = loadBalancer.choose("service-product");
        System.out.println("choose: " + choose.getHost() + ":" + choose.getPort());
    }
}
