package com.ral.admin.webflux.service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-12 16:00
 * @Describe:
 * @Modify:
 */
@Service
public class WebService {

    @Resource
    private LightService lightService;

    public WebService() {
        lightService.start();
    }
}
