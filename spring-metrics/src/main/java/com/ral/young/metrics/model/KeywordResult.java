package com.ral.young.metrics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 关键字提取结果
 */
@Data
public class KeywordResult {
    /**
     * System部分的关键字
     */
    private List<KeyValue> systemKeywords;

    /**
     * Response部分的关键字
     */
    private List<List<KeyValue>> responseKeywords;

    /**
     * 键值对实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KeyValue {
        private String key;
        private String value;
    }
}