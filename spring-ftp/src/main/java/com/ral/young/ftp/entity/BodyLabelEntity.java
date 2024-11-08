package com.ral.young.ftp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyLabelEntity {

    private Integer index;          // 标签索引(每个分类下从0开始)

    private Integer parentIndex;    // 父标签索引(一级分类的索引)

    private String englishName;     // 英文名

    private String chineseName;     // 中文名

    private String category;        // 分类名称
}