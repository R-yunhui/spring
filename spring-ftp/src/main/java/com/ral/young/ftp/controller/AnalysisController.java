package com.ral.young.ftp.controller;

import com.ral.young.ftp.service.AnalysisService;
import com.ral.young.ftp.vo.AnalysisQueryVO;
import com.ral.young.ftp.vo.AnalysisVO;
import com.ral.young.ftp.vo.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @description AnalysisController
 * @date 2024-11-01 15-11-23
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/analysis")
public class AnalysisController {

    @Resource
    private AnalysisService analysisService;

    @GetMapping(value = "/api/v1/image/result/{imgName}")
    public ResultVO<AnalysisVO> result(@PathVariable(value = "imgName") String imgName) {
        return analysisService.result(imgName);
    }

    @PostMapping(value = "/api/v1/image/query")
    public ResultVO<Void> analyze(@RequestBody AnalysisQueryVO analysisQueryVO) {
        analysisService.analyze(analysisQueryVO);
        return ResultVO.success(null);
    }
}
