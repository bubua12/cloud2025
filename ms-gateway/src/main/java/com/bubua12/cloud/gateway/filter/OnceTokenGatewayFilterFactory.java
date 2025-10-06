package com.bubua12.cloud.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 *
 *
 * @author bubua12
 * @since 2025/8/16 19:07
 */
@Component
public class OnceTokenGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {

    @Override
    public GatewayFilter apply(NameValueConfig config) {

        return (exchange, chain) -> {
            // 每次响应之前，添加一个一次性令牌，支持uuid、jwt等各种格式的令牌
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        ServerHttpResponse response = exchange.getResponse();
                        HttpHeaders headers = response.getHeaders();
                        String value = config.getValue();
                        if ("uuid".equalsIgnoreCase(value)) {
                            value = UUID.randomUUID().toString();
                        }
                        if ("jwt".equalsIgnoreCase(value)) {
                            value = "eyJ1c2VySWQiOiIxMjM0NTYiLCJ1c2VybmFtZSI6ImJ1YnUiLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MjM4MDk2MDAsImV4cCI6MTcyMzgxMzIwMH0.";
                        }

                        headers.add(config.getName(), value);
                    }));
        };
    }
}
