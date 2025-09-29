package com.bubua12.cloud.order.config;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 事务级别的追踪切面
 * 用于追踪Spring事务的开始、提交、回滚等操作
 *
 * @author bubua12
 * @since 2025/9/29
 */
@Aspect
@Component
public class TransactionTracingAspect {

    private static final Logger log = LoggerFactory.getLogger(TransactionTracingAspect.class);

    @Autowired(required = false)
    private Tracer tracer;

    /**
     * 拦截所有带有@Transactional注解的方法
     */
    @Around("@annotation(transactional)")
    public Object traceTransactionalMethod(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        if (tracer == null) {
            return joinPoint.proceed();
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        Span span = tracer.nextSpan()
                .name("transaction." + className + "." + methodName)
                .tag("transaction.type", "spring")
                .tag("transaction.propagation", transactional.propagation().name())
                .tag("transaction.isolation", transactional.isolation().name())
                .tag("transaction.read-only", String.valueOf(transactional.readOnly()))
                .tag("transaction.timeout", String.valueOf(transactional.timeout()))
                .tag("service.name", "seata-order")
                .tag("method.class", className)
                .tag("method.name", methodName)
                .start();

        try {
            Object result = joinPoint.proceed();
            
            span.tag("transaction.status", "committed");
            log.debug("事务完成 - 方法: {}.{}, 传播级别: {}", 
                    className, methodName, transactional.propagation().name());
            
            return result;
        } catch (Throwable e) {
            span.tag("error", true)
                .tag("error.type", e.getClass().getSimpleName())
                .tag("error.message", e.getMessage())
                .tag("transaction.status", "rolled-back");
            
            log.error("事务失败 - 方法: {}.{}, 错误: {}", 
                    className, methodName, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    /**
     * 拦截Service层的所有方法（通常包含业务事务）
     */
    @Around("execution(* com.bubua12.cloud.order.service..*(..))")
    public Object traceServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (tracer == null) {
            return joinPoint.proceed();
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        Span span = tracer.nextSpan()
                .name("service." + className + "." + methodName)
                .tag("service.layer", "business")
                .tag("service.name", "seata-order")
                .tag("method.class", className)
                .tag("method.name", methodName)
                .tag("method.args.count", String.valueOf(args != null ? args.length : 0))
                .start();

        try {
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            span.tag("method.duration.ms", String.valueOf(duration))
                .tag("method.status", "success");
            
            // 性能分级
            if (duration > 2000) {
                span.tag("method.performance", "slow");
            } else if (duration > 1000) {
                span.tag("method.performance", "medium");
            } else {
                span.tag("method.performance", "fast");
            }
            
            log.debug("业务方法完成 - 方法: {}.{}, 耗时: {}ms", 
                    className, methodName, duration);
            
            return result;
        } catch (Throwable e) {
            span.tag("error", true)
                .tag("error.type", e.getClass().getSimpleName())
                .tag("error.message", e.getMessage())
                .tag("method.status", "error");
            
            log.error("业务方法失败 - 方法: {}.{}, 错误: {}", 
                    className, methodName, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}