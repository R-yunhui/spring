package com.ral.young.handler;

import com.ral.young.result.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局统一异常处理
 *
 * @author renyunhui
 * @date 2023-06-25 11:09
 * @since 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public BaseResult<String> exceptionHandler(HttpServletRequest req, Exception e) {
        log.error("业务异常,errorMsg:{}", e.getMessage(), e);
        // 可以解析请求的部分信息进行打印
        return BaseResult.error(e.getMessage());
    }
}
