package com.ral.young.spring.basic.common;

import com.ral.young.spring.basic.exception.IErrorCode;
import lombok.Data;

@Data
public class Result<T> {

    private Integer code;

    private String message;

    private T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(IErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
} 