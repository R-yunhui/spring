package com.ral.young.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author renyunhui
 * @description 这是一个MethodCountController类
 * @date 2024-08-15 10-31-28
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/method")
@Slf4j
public class MethodCountController {

    /**
     * 引入micrometer的工具类
     */
    @Resource
    private MeterRegistry meterRegistry;

    @GetMapping("/deleteCount")
    public Object deleteCount() {
        log.info("delete-----{}", LocalDateTime.now());
        incrementMonitorCount("com.ral.young.controller.MethodCountController.deleteCount");
        return "deleteCount-----" + LocalDateTime.now();
    }

    @GetMapping("/addCount")
    public Object addCount() {
        log.info("add-----{}", LocalDateTime.now());
        incrementMonitorCount("com.ral.young.controller.MethodCountController.addCount");
        return "addCount-----" + LocalDateTime.now();
    }


    /**
     * 记录接口调用次数
     *
     * @param method 方法名
     */
    public void incrementMonitorCount(String method) {
        //定义指标名称
        String metricName = "method_count";
        Counter counter = meterRegistry.counter(metricName, "methodName", method);
        counter.increment();
    }

    /*********************************************************************************************/
    /********************************上面是接口调用次数，下面是接口调用耗时******************************/
    /*********************************************************************************************/

    @GetMapping("/deleteRt")
    public Object deleteRt() {
        log.info("deleteRt-----{}", LocalDateTime.now());
        recordMethodRt("com.ral.young.controller.MethodCountController.deleteRt", 20L);
        return "deleteRt-----" + LocalDateTime.now();
    }

    @GetMapping("/addRt")
    public Object addRt() {
        log.info("addRt-----{}", LocalDateTime.now());
        recordMethodRt("com.ral.young.controller.MethodCountController.addRt", 10L);
        return "addRt-----" + LocalDateTime.now();
    }

    /**
     * 记录接口的RT
     *
     * @param method 方法名
     * @param rt     时间
     */
    public void recordMethodRt(String method, Long rt) {
        //定义指标名称
        String metricName = "method_rt";
        meterRegistry.timer(metricName, Tags.of("methodName", method)).record(rt, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
}
