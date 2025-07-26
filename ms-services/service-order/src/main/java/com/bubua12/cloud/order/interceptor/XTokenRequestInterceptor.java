package com.bubua12.cloud.order.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 请求拦截器
 * 1、yaml配置文件中针对特定的Feign客户端添加配置
 * 2、会自动在容器中找这些组件：spring-cloud-feign-overriding-defaults... ... (容器中只要有这个组件，会自动应用上)
 * 3、
 *
 * @author bubua12
 * @since 2025/7/27 4:14
 */
@Component
public class XTokenRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        System.out.println("O(∩_∩)O... ... x-token interceptor start apply... ...");
        requestTemplate.header("X-Token", UUID.randomUUID().toString());
    }
}
