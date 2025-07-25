package com.bubua12.cloud.order.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置批量绑定 Nacos下无需@RefreshScope即可自动刷新
 *
 * @author bubua12
 * @since 2025/7/25 23:23
 */
@Component
@ConfigurationProperties(prefix = "order")
@Data
public class OrderProperties {
    private String timeout;
    private String autoConfirm;
    private String dbUrl;
}
