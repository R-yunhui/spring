package com.ral.young.practice.listener;

import org.springframework.context.ApplicationEvent;

/**
 *
 * @author renyunhui
 * @date 2022-11-16 15:57
 * @since 1.0.0
 */
public class CustomEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public CustomEvent(Object source) {
        super(source);
    }
}
