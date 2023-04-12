package com.ral.young.practice;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2023-04-12 10:22
 * @since 1.0.0
 */
@Component
@Data
public class Car {

    private int id;

    private String name;
}
