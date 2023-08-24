package com.ral.young.user.controller;

import cn.hutool.core.date.DateUtil;
import com.ral.young.result.BaseResult;
import com.ral.young.user.dto.UserDTO;
import com.ral.young.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author renyunhui
 * @date 2023-06-20 14:41
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/user")
@Slf4j
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping(value = "/register")
    public BaseResult<String> register(@RequestBody UserDTO userDTO) {
        String token = userService.registerUser(userDTO);
        return BaseResult.success(token);
    }

    @GetMapping(value = "/getUser/{id}")
    public BaseResult<UserDTO> getUser(@PathVariable(value = "id") Long userId) {
        return BaseResult.success(userService.getUserById(userId));
    }

    @GetMapping(value = "/mockError")
    public BaseResult<String> mockError() {
        int a = ThreadLocalRandom.current().nextInt(10) + 1;
        if (a > 3) {
            throw new RuntimeException("异常了");
        }
        return BaseResult.success("mockError");
    }

    @GetMapping(value = "/mockSleep")
    public BaseResult<String> mockSleep() throws InterruptedException {
        log.info("线程名称:{},时间;{}", Thread.currentThread().getName(), DateUtil.now());
        TimeUnit.MINUTES.sleep(2);
        log.info("开始执行业务请求,时间:{}", DateUtil.now());
        return BaseResult.success();
    }
}
