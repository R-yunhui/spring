package com.ral.young.study.spring.ioctwo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2023-11-21 11:02
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "tank")
@Component
@Data
public class Tank {

    private String name;

    private int id;
}
