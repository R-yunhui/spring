package com.ral.young.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Aop 切面
 *
 * @author renyunhui
 * @date 2022-07-01 15:41
 * @since 1.0.0
 */
@Aspect
@Component
@Slf4j
@Order(value = 1)
public class MyLogAspect {

    @Pointcut(value = "@annotation(com.ral.young.aop.annotation.MyLog)")
    public void pointCut() {
    }

    @Before(value = "pointCut()")
    public void beforeLog(JoinPoint joinPoint) {
        log.info("MyLogAspect.beforeLog()");
    }

    @After(value = "pointCut()")
    public void afterLog(JoinPoint joinPoint) {
        log.info("MyLogAspect.afterLog()");
    }

    @AfterReturning(value = "pointCut()")
    public void afterLogReturning(JoinPoint joinPoint) {
        log.info("MyLogAspect.afterLogReturning()");
    }

    @AfterReturning(value = "pointCut()")
    public void afterLogError(JoinPoint joinPoint) {
        log.info("MyLogAspect.afterLogError()");
    }
}
