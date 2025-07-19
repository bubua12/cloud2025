package com.bubua12.cloud.product;

import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

/**
 * DiscoveryClient: Spring家的规范、任何注册中心都适用
 * NacosServiceDiscovery: 引入Nacos时能用的API
 *
 * @author bubua12
 * @since 2025/7/19 9:07
 */
@SpringBootTest
public class ServiceDiscoveryTests {
    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    NacosServiceDiscovery nacosServiceDiscovery;


    /**
     * 测试Nacos服务发现API
     *
     * @throws NacosException NacosException
     */
    @Test
    void nacosServiceDiscoveryTest() throws NacosException {
        for (String service : nacosServiceDiscovery.getServices()) {
            System.out.println("service: " + service);
            List<ServiceInstance> instances = nacosServiceDiscovery.getInstances(service);
            for (ServiceInstance instance : instances) {
                System.out.println("ip: " + instance.getHost() + " port: " + instance.getPort());
            }
        }
    }

    /**
     * 测试Spring服务发现API
     */
    @Test
    void discoveryClient() {
        for (String service : discoveryClient.getServices()) {
            System.out.println("service: " + service);

            // 获取IP和端口
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            for (ServiceInstance instance : instances) {
                System.out.println("ip: " + instance.getHost() + " port: " + instance.getPort());
            }
        }
    }

}
