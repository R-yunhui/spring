package com.ral.young.spring.event;

import org.springframework.context.ApplicationEvent;

/**
 * 自定义数据改变的事件
 *
 * @author renyunhui
 * @date 2023-02-16 16:27
 * @since 1.0.0
 */
public class DataChangedEvent extends ApplicationEvent {

    private String msg;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DataChangedEvent(Object source) {
        super(source);
    }

    public DataChangedEvent(Object source, String msg) {
        super(source);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
