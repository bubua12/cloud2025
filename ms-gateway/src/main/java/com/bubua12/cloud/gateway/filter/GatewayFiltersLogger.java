package com.bubua12.cloud.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebFilter;

import java.util.Arrays;
import java.util.Map;

/**
 *
 *
 * @author bubua12
 * @since 2025/8/9 21:40
 */
@Component
public class GatewayFiltersLogger implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(GatewayFiltersLogger.class);

    @Autowired
    private ApplicationContext ctx;

    @Autowired(required = false)
    private RouteDefinitionLocator routeDefinitionLocator;

    @Autowired(required = false)
    private RouteLocator routeLocator;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== Start: dump GlobalFilter beans ===");
        Map<String, GlobalFilter> globalFilters = ctx.getBeansOfType(GlobalFilter.class);
        globalFilters.forEach((name, bean) -> {
            int order = (bean instanceof Ordered) ? ((Ordered) bean).getOrder() : Ordered.LOWEST_PRECEDENCE;
            log.info("GlobalFilter bean: name='{}', class='{}', order={}", name, bean.getClass().getName(), order);
        });

        log.info("=== Start: dump GatewayFilterFactory beans ===");
        Map<String, GatewayFilterFactory> factories = ctx.getBeansOfType(GatewayFilterFactory.class);
        factories.forEach((name, bean) -> log.info("GatewayFilterFactory bean: name='{}', class='{}'", name, bean.getClass().getName()));

        log.info("=== Start: dump WebFilter beans ===");
        Map<String, WebFilter> webFilters = ctx.getBeansOfType(WebFilter.class);
        webFilters.forEach((name, bean) -> log.info("WebFilter bean: name='{}', class='{}'", name, bean.getClass().getName()));

        log.info("=== Start: scan bean names mention observation/trace/filter ===");
        Arrays.stream(ctx.getBeanDefinitionNames())
                .filter(n -> n.toLowerCase().contains("observation") || n.toLowerCase().contains("trace") || n.toLowerCase().contains("filter"))
                .sorted()
                .forEach(n -> log.info("maybe interesting bean: {}", n));

        if (routeDefinitionLocator != null) {
            log.info("=== RouteDefinitionLocator: list route definitions and declared filters ===");
            routeDefinitionLocator.getRouteDefinitions()
                    .collectList()
                    .doOnNext(list -> list.forEach(rd -> {
                        log.info("RouteDefinition id='{}', uri='{}', filters={}", rd.getId(), rd.getUri(), rd.getFilters());
                    })).block();
        }

        if (routeLocator != null) {
            log.info("=== RouteLocator: list runtime routes and filters (toString may be less verbose) ===");
            routeLocator.getRoutes().collectList().doOnNext(list ->
                    list.forEach(rt -> log.info("Runtime route id='{}', uri='{}', filters={}", rt.getId(), rt.getUri(), rt.getFilters()))
            ).block();
        }

        log.info("=== End: dump filters ===");
    }
}