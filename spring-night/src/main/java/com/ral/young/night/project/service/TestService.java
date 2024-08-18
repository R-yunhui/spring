package com.ral.young.night.project.service;

import com.ral.young.night.project.event.InitUserEvent;
import com.ral.young.night.project.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author renyunhui
 * @date 2024-06-26 14:39
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestService implements ApplicationContextAware, CommandLineRunner {

    private ApplicationContext applicationContext;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    private List<User> userList;

    @PostConstruct
    public void doPostConstruct() {
        log.info("TestService doPostConstruct");
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void test() {
        userList.forEach(user -> log.info("用户信息：{}", user));
    }

    @Override
    public void run(String... args) throws Exception {
        long count = userService.count();
        log.info("查询到的用户信息数量：{}", count);
        int initUserSize = 0;
        if (count > 0) {
            userList = userService.list();
        } else {
            // 初始化 10w 数据入库
            int size = 100000;
            initUserSize = userService.initUserInfo(size);
            log.info("构造：{} 用户数据完成", initUserSize);
        }
        applicationEventPublisher.publishEvent(new InitUserEvent(initUserSize));
        userService.updateBatchUser();
        TimeUnit.MINUTES.sleep(10);
    }

    @EventListener
    public void testTwo(InitUserEvent event) {
        log.info("监听到初始化用户数据完成，初始化用户数量：{}" , event.getInitUserSize());
    }
}
