package com.ral.young.night.project.event;

import org.springframework.context.ApplicationEvent;

/**
 *
 * @author renyunhui
 * @date 2024-06-26 15:25
 * @since 1.0.0
 */
public class InitUserEvent extends ApplicationEvent {

    int initUserSize;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public InitUserEvent(Object source) {
        super(source);
        this.initUserSize = (int) source;
    }

    public int getInitUserSize() {
        return initUserSize;
    }
}
