package com.ral.young.spring.basic.metrics.aspect;

import com.ral.young.spring.basic.metrics.annotation.MetricsMonitor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    public MetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("@annotation(metricsMonitor)")
    public Object recordMetrics(ProceedingJoinPoint joinPoint, MetricsMonitor metricsMonitor) throws Throwable {
        String name = metricsMonitor.name();
        String description = metricsMonitor.description();
        String[] tags = metricsMonitor.tags();

        // 创建计数器构建器
        Counter.Builder counterBuilder = Counter.builder(name)
                .description(description);

        // 添加公共标签
        for (String tag : tags) {
            String[] parts = tag.split("=");
            if (parts.length == 2) {
                counterBuilder.tag(parts[0], parts[1]);
            }
        }

        try {
            // 执行原方法
            Object result = joinPoint.proceed();
            // 增加成功计数
            counterBuilder.tag("status", "success")
                    .register(meterRegistry)
                    .increment();
            return result;
        } catch (Exception e) {
            // 增加失败计数
            counterBuilder.tag("status", "failure")
                    .register(meterRegistry)
                    .increment();
            log.error("方法执行异常: {}.{}", 
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), 
                    e);
            throw e;
        }
    }
} 