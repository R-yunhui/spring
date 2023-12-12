package com.ral.young.study.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author renyunhui
 * @date 2023-12-11 15:28
 * @since 1.0.0
 */
@Component
@Slf4j
public class TestFunction implements CommandLineRunner {

    public final Map<String, Function<Double, Double>> functionMap = new HashMap<>(16);

    @Resource
    private MemberService memberService;

    @PostConstruct
    public void init() {
        // 使用这种方式可以优化策略模式，防止出现太多的策略类
        functionMap.put("高级会员", (price) -> memberService.getHighOrder(price));
        functionMap.put("中级会员", (price) -> memberService.getMidOrder(price));
        functionMap.put("普通会员", (price) -> memberService.getNormalOrder(price));
    }

    @Override
    public void run(String... args) {
        double price = 100.0;
        Double total = functionMap.get("高级会员").apply(price);
        log.info("高级会员需要支付：" + total);
        total = functionMap.get("中级会员").apply(price);
        log.info("中级会员需要支付：" + total);
        total = functionMap.get("普通会员").apply(price);
        log.info("普通会员需要支付：" + total);
    }
}
