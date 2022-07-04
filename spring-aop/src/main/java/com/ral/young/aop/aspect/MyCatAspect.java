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
@Order(value = 2)
public class MyCatAspect {

    @Pointcut(value = "@annotation(com.ral.young.aop.annotation.MyCat)")
    public void pointCut() {

    }

    @Before(value = "pointCut()")
    public void beforeLog(JoinPoint joinPoint) {
        log.info("MyCatAspect.beforeLog()");
    }

    @After(value = "pointCut()")
    public void afterLog(JoinPoint joinPoint) {
        log.info("MyCatAspect.afterLog()");
    }

    @AfterReturning(value = "pointCut()")
    public void afterLogReturning(JoinPoint joinPoint) {
        log.info("MyCatAspect.afterLogReturning()");
    }

    @AfterReturning(value = "pointCut()")
    public void afterLogError(JoinPoint joinPoint) {
        log.info("MyCatAspect.afterLogError()");
    }
}
