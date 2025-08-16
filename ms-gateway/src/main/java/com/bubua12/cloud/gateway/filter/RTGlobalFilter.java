package com.bubua12.cloud.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * 全局过滤器不用做任何配置，直接访问就行
 *
 * @author bubua12
 * @since 2025/8/16 18:33
 */
@Component
public class RTGlobalFilter implements GlobalFilter, Ordered {
    public static Logger log = LoggerFactory.getLogger(RTGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();
        log.info("请求 {} 开始，时间: {}", request.getURI(), LocalDateTime.now());
        // ========================================== 以上是前置逻辑

        return chain.filter(exchange)
                // ========================================== 以下是后置逻辑
                .doFinally((result) -> {
                            long endTime = System.currentTimeMillis();
                            log.info("请求 {} 结束，时间：{}，耗时: {} ms", request.getURI(), LocalDateTime.now(), endTime - startTime);
                        }
                );
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
