package com.ral.young.aop.proxy.statics;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author renyunhui
 * @date 2022-07-06 13:43
 * @since 1.0.0
 */
@Slf4j
public class Dog implements Animal {

    @Override
    public void call() {
        log.info("汪汪汪 ~~~");
    }
}
