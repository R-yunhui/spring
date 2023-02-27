package com.ral.young.spring.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2023-02-17 10:57
 * @since 1.0.0
 */
@Aspect
@Slf4j
@Component
public class AopAspectOne {

    @Pointcut(value = "@annotation(com.ral.young.spring.aop.annotation.Log)")
    public void pointCut() {

    }

    @Before(value = "pointCut()")
    public void beforeLog(JoinPoint joinPoint) {
        log.info("AopAspectOne.beforeLog()");
    }

    @After(value = "pointCut()")
    public void afterLog(JoinPoint joinPoint) {
        log.info("AopAspectOne.afterLog()");
    }

    @AfterReturning(value = "pointCut()")
    public void afterLogReturning(JoinPoint joinPoint) {
        log.info("AopAspectOne.afterLogReturning()");
    }

    @AfterThrowing(value = "pointCut()")
    public void afterLogError(JoinPoint joinPoint) {
        log.info("AopAspectOne.afterLogError()");
    }

}
