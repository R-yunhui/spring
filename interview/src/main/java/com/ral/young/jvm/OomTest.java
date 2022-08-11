package com.ral.young.jvm;

import cn.hutool.core.util.IdUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存溢出测试
 *
 * @author renyunhui
 * @date 2022-08-01 9:35
 * @since 1.0.0
 */
public class OomTest {

    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (true) {
            list.add(new User(i++, IdUtil.fastSimpleUUID()));
            new User(j--, IdUtil.fastSimpleUUID());
        }
    }
}
