package com.ral.young.practice.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2022-11-30 18:51
 * @since 1.0.0
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    private String name;

    private int age;
}
