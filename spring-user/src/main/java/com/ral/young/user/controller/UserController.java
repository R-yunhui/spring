package com.ral.young.user.controller;

import com.ral.young.result.BaseResult;
import com.ral.young.user.dto.UserDTO;
import com.ral.young.user.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * @author renyunhui
 * @date 2023-06-20 14:41
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/user")
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
}
