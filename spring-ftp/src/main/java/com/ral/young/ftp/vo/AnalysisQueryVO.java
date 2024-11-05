package com.ral.young.ftp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个AnalysisQueryVO类
 * @date 2024-11-05 15-27-05
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalysisQueryVO {

    private String imgName;

    private String imgBase64;
}
