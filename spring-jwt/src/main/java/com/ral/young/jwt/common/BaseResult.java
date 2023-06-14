package com.ral.young.jwt.common;

/**
 * @author renyunhui
 * @date 2023-06-08 15:01
 * @since 1.0.0
 */
public class BaseResult<T> {

    private T data;

    private Integer code;

    private String message;

    public BaseResult(T data, Integer code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(data, 200, "ok");
    }

    public static <T> BaseResult<T> success() {
        return new BaseResult<>(null, 200, "ok");
    }

    public static <T> BaseResult<T> error(String errorMessage) {
        return new BaseResult<>(null, 500, errorMessage);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
