package com.ral.young.basic.spi;

import java.util.List;

/**
 * 文件搜索
 *
 * @author renyunhui
 * @date 2023-07-05 10:28
 * @since 1.0.0
 */
public class FileSearch implements Search {

    @Override
    public List<String> searchDoc(String name) {
        System.out.println("文件搜索 " + name);
        return null;
    }
}
