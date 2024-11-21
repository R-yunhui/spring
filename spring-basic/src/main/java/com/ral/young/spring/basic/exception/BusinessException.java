package com.ral.young.spring.basic.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final String message;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
} 