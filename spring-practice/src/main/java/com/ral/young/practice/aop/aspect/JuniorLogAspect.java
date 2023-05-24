package com.ral.young.practice.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2023-05-24 10:57
 * @since 1.0.0
 */
@Aspect
@Component
@Slf4j
public class JuniorLogAspect {

    @Pointcut("@annotation(com.ral.young.practice.aop.annotation.JuniorLog)")
    public void pointCut() {

    }

    @Before(value = "pointCut()")
    public void beforeLog(JoinPoint joinPoint) {
        log.info("JuniorLogAspect#前置通知");
    }

    @After(value = "pointCut()")
    public void afterLog(JoinPoint joinPoint) {
        log.info("JuniorLogAspect#后置通知)");
    }

    @AfterReturning(value = "pointCut()")
    public void afterLogReturning(JoinPoint joinPoint) {
        log.info("JuniorLogAspect#返回通知");
    }

}
