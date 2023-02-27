package com.ral.young.spring.event;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.ErrorHandler;

/**
 * Spring 事件处理的统一异常处理器
 *
 * @author renyunhui
 * @date 2023-02-16 16:25
 * @since 1.0.0
 */
@Slf4j
public class ApplicationEventMulticasterErrorHandler implements ErrorHandler {
    @Override
    public void handleError(@NotNull Throwable t) {
        log.error("事件处理过程中出现异常,errorMsg:{}", t.getMessage(), t);
    }
}
