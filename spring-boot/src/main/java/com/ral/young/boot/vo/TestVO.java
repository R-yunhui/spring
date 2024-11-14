package com.ral.young.boot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个TestVO类
 * @date 2024-11-14 14-00-05
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestVO {

    private String imgUrl;

    private String token;

    private String boxes;

    private byte local;
}
