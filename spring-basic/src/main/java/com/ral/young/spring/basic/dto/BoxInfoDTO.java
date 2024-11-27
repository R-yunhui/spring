package com.ral.young.spring.basic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个BoxInfoDTO类
 * @date 2024-11-27 14-50-33
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoxInfoDTO {

    private Integer width;

    private Integer height;

    private Integer x;

    private Integer y;

    private String text;

    private String boxColor;

    private String textColor;

    private Integer fontSize;
}
