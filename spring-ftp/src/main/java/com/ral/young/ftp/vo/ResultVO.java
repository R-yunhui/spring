package com.ral.young.ftp.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * @author renyunhui
 * @description 这是一个ResultVO类
 * @date 2024-11-01 14-38-13
 * @since 1.0.0
 */
@Data
public class ResultVO<T> implements Serializable {

    private Integer status;

    private String message;

    private T data;

    /**
     * 是否成功
     *
     * @return 成功则返回 true
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.getStatus() == 200;
    }

    /**
     * 是否失败
     *
     * @return 失败则返回 true
     */
    @JsonIgnore
    public boolean isFailure() {
        return this.getStatus() != 200;
    }

    public static <T> ResultVO<T> success(T data) {
        ResultVO<T> vo = new ResultVO<>();
        vo.setStatus(200);
        vo.setMessage("success");
        vo.setData(data);
        return vo;
    }

    public static <T> ResultVO<T> failure(Integer status, String message) {
        ResultVO<T> vo = new ResultVO<>();
        vo.setStatus(status);
        vo.setMessage(message);
        vo.setData(null);
        return vo;
    }

    public static <T> ResultVO<T> failure(Integer status, String message, T data) {
        ResultVO<T> vo = new ResultVO<>();
        vo.setStatus(status);
        vo.setMessage(message);
        vo.setData(data);
        return vo;
    }
}
