package com.ral.young.metrics.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> list;        // 当前页数据
    private long total;           // 总记录数
    private int page;            // 当前页码
    private int size;            // 每页大小
    private int pages;           // 总页数
}
