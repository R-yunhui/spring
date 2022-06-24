package com.ral.young.eventlistener;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * 自定义消息事件
 *
 * @author renyunhui
 * @date 2022-06-23 19:07
 * @since 1.0.0
 */
@Slf4j
@Getter
public class MessageEvent extends ApplicationEvent {

    public MessageEvent(Object source) {
        super(source);
    }
}
