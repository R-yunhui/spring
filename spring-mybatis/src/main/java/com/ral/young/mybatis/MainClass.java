package com.ral.young.mybatis;

import cn.hutool.core.util.IdUtil;

/**
 * TODO
 *
 * @author renyunhui
 * @date 2023-06-08 15:09
 * @since 1.0.0
 */
public class MainClass {

    public static void main(String[] args) {
        System.out.println(IdUtil.getSnowflakeNextId());
    }
}
