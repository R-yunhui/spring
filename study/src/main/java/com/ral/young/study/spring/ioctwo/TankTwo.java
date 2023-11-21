package com.ral.young.study.spring.ioctwo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @link @PropertySource 以及 @Value 配合使用注入外部配置文件的属性
 *
 * @author renyunhui
 * @date 2023-11-21 11:06
 * @since 1.0.0
 */
@PropertySource(value = "classpath:config/TankTwo.yml")
@Component
@Data
public class TankTwo {

    @Value("${id}")
    private int id;

    @Value("${name}")
    private String name;
}
