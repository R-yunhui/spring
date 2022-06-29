package com.ral.young.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 应用上下文刷新完成的事件监听器
 *
 * @author renyunhui
 * @date 2022-06-29 17:05
 * @since 1.0.0
 */
@Component
@Slf4j
public class ApplicationContextRefreshListener {

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Spring中所有Bean初始化完成,监听 ContextRefreshEvent 事件,进行回调");
    }
}
