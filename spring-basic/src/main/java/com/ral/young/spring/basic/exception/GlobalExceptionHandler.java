package com.ral.young.spring.basic.exception;

import com.ral.young.spring.basic.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
        log.error("参数校验异常：{}", message, e);
        return Result.error(ErrorCodeEnum.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
        log.error("参数绑定异常：{}", message, e);
        return Result.error(ErrorCodeEnum.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Result.error(ErrorCodeEnum.SYSTEM_ERROR);
    }
} 