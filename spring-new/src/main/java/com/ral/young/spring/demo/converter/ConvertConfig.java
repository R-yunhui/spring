package com.ral.young.spring.demo.converter;

import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 转换配置类
 */
@Data
@Builder
public class ConvertConfig {

    @Builder.Default
    private boolean prettyPrint = true;

    @Builder.Default
    private String charset = StandardCharsets.UTF_8.name();

    @Builder.Default
    private int retryTimes = 3;

    private Map<String, String> labelCodeMapping;
} 