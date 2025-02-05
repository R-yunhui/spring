package com.ral.young.spring.controller;

import com.ral.young.spring.service.EventSseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @description 控制器层 demo
 * @date 2025-01-20 11-41-38
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/web")
public class WebController {

    @Resource
    private EventSseService eventSseService;

    @GetMapping(value = "/sayHello")
    public String sayHello() {
        return "hello world";
    }

    @GetMapping(value = "/api/v1/event/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        return eventSseService.createEmitter();
    }
}
