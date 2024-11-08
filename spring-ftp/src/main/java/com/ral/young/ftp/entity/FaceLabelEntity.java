package com.ral.young.ftp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceLabelEntity {

    private Integer index;          // 标签索引

    private String englishName;     // 英文名

    private String chineseName;     // 中文名
}