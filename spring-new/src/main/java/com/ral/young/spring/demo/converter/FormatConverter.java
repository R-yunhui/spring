package com.ral.young.spring.demo.converter;

/**
 * 格式转换接口
 */
public interface FormatConverter {

    String convert(String input, ConvertConfig config);
} 