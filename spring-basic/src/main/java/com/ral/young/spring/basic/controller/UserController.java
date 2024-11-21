package com.ral.young.spring.basic.controller;

import com.ral.young.spring.basic.entity.User;
import com.ral.young.spring.basic.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理API")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping
    @ApiOperation(value = "获取用户列表", notes = "返回所有用户的列表")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功返回用户列表"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public List<User> list() {
        return userService.list();
    }

    @PostMapping
    @ApiOperation(value = "保存用户信息", notes = "保存新的用户信息")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功保存用户信息"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public boolean save(@RequestBody @ApiParam(value = "用户信息", required = true) User user) {
        return userService.save(user);
    }

    @PutMapping
    @ApiOperation(value = "更新用户信息", notes = "更新现有用户的信息")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功更新用户信息"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public boolean update(@RequestBody @ApiParam(value = "用户信息", required = true) User user) {
        return userService.updateById(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除用户", notes = "根据用户ID删除用户")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功删除用户"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public boolean delete(@PathVariable @ApiParam(value = "用户ID", required = true) Long id) {
        return userService.removeById(id);
    }
}