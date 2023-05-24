package com.ral.young.sprint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author renyunhui
 * @date 2023-05-23 11:08
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    private Long id;

    private String name;

    private Car car;
}
