package com.ral.young.basic.spi;

import java.util.List;

/**
 * 数据库搜索
 *
 * @author renyunhui
 * @date 2023-07-05 10:29
 * @since 1.0.0
 */
public class DataSourceSearch implements Search {
    @Override
    public List<String> searchDoc(String name) {
        System.out.println("数据库搜索 " + name);
        return null;
    }
}
