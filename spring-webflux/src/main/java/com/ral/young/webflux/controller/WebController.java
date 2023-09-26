package com.ral.young.webflux.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * controller
 *
 * @author renyunhui
 * @date 2023-09-14 15:57
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/web")
@Slf4j
public class WebController {

    @RequestMapping(value = "/testWebMvc")
    public String testWebMvc() throws InterruptedException {
        log.info("{}:testWebMvc Start", Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(5);
        log.info("{}:testWebMvc end", Thread.currentThread().getName());
        return "testWebMvc";
    }

    @RequestMapping(value = "/testWebFlux")
    public Mono<String> testWebFlux() {
        log.info("{}:testWebFlux Start", Thread.currentThread().getName());
        Mono<String> mono = Mono.fromSupplier(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ignored) {
            }
            return "testWebFlux";
        });
        log.info("{}:testWebFlux end", Thread.currentThread().getName());
        return mono;
    }

    @GetMapping(value = "/testFlux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<String> testFlux() {
        log.info("{}:testFlux Start", Thread.currentThread().getName());
        Flux<String> result = Flux.fromStream(IntStream.range(1, 5).mapToObj(i -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ignored) {
            }
            return "flux data--" + i;
        }));
        log.info("{}:testFlux end", Thread.currentThread().getName());
        return result;
    }
}
