package com.ral.young.spring.basic.exception;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum implements IErrorCode {

    // 系统级别错误
    SYSTEM_ERROR(500, "系统内部错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    // 业务级别错误
    USER_NOT_FOUND(40001, "用户不存在"),
    USER_ALREADY_EXISTS(40002, "用户已存在"),
    PASSWORD_ERROR(40003, "密码错误"),
    USER_EMAIL_EXISTS(40002, "邮箱已被使用"),
    USER_CREATE_ERROR(40003, "用户创建失败"),
    USER_UPDATE_ERROR(40004, "用户更新失败"),

    // 批量插入错误
    BATCH_INSERT_ERROR(50001, "批量插入数据失败");

    private final Integer code;
    private final String message;

    ErrorCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
} 