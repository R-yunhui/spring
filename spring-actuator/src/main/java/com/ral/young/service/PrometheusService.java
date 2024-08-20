package com.ral.young.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @description 这是一个TestService类
 * @date 2024-08-19 15-31-39
 * @since 1.0.0
 */
@Service
@Slf4j
public class PrometheusService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
}
