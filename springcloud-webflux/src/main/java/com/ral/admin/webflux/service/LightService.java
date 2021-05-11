package com.ral.admin.webflux.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-12 16:00
 * @Describe:
 * @Modify:
 */
@Service
@Slf4j
public class LightService {

    public void start() {
        log.info("turn on all lights");
    }

    public void shutdown() {
        log.info("turn off all lights");
    }

    public void check() {
        log.info("check all lights");
    }
}
