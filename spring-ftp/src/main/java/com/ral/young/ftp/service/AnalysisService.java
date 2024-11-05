package com.ral.young.ftp.service;

import com.ral.young.ftp.vo.AnalysisQueryVO;
import com.ral.young.ftp.vo.AnalysisVO;
import com.ral.young.ftp.vo.BigModelAnalysisVO;
import com.ral.young.ftp.vo.BigModelQueryVO;
import com.ral.young.ftp.vo.CVModelResultVO;
import com.ral.young.ftp.vo.ResultVO;

/**
 * @author renyunhui
 * @description 这是一个TestService类
 * @date 2024-11-01 14-36-54
 * @since 1.0.0
 */
public interface AnalysisService {

    BigModelAnalysisVO executeBigModel(BigModelQueryVO modelQueryVO);

    CVModelResultVO executeCVModel();

    void analyze(AnalysisQueryVO analysisQueryVO);

    ResultVO<AnalysisVO> result(String imgName);
}
