package com.ral.young.ftp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ral.young.ftp.service.AnalysisService;
import com.ral.young.ftp.vo.AnalysisQueryVO;
import com.ral.young.ftp.vo.AnalysisVO;
import com.ral.young.ftp.vo.BigModelAnalysisVO;
import com.ral.young.ftp.vo.BigModelQueryVO;
import com.ral.young.ftp.vo.CVModelResultVO;
import com.ral.young.ftp.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author renyunhui
 * @description 这是一个TestServiceImpl类
 * @date 2024-11-01 15-09-15
 * @since 1.0.0
 */
@Service
@Slf4j
public class AnalysisServiceImpl implements AnalysisService {

    @Resource
    private RestTemplate restTemplate;

    public static final String BIG_MODEL_URL = "http://192.168.2.57:23003/v1/chat/completions";

    public static final String CV_MODEL_URL = "http://10.10.1.60:8087/statical/api/v1/body/attribute";

    public static Map<String, ResultVO<AnalysisVO>> cacheMap = new HashMap<>(8);

    @Override
    public BigModelAnalysisVO executeBigModel(BigModelQueryVO modelQueryVO) {
        try {
            JSONObject param = JSONUtil.parseObj(modelQueryVO);
            log.info("调用大模型接口请求参数：{}", param);
            ResponseEntity<BigModelAnalysisVO> response = restTemplate.postForEntity(BIG_MODEL_URL, param, BigModelAnalysisVO.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.info("调用大模型接口请求失败");
                return null;
            }

            BigModelAnalysisVO body = response.getBody();
            log.info("调用大模型接口返回结果：{}", JSONUtil.toJsonStr(body));
            return body;
        } catch (Exception e) {
            log.error("调用大模型接口失败：", e);
        }
        return null;
    }

    @Override
    public CVModelResultVO executeCVModel(String base64) {
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 创建请求体
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("baseLs", base64);
        HttpEntity<MultiValueMap<String, Object>> multiValueMapHttpEntity = new HttpEntity<>(paramMap, headers);
        try {
            ResponseEntity<CVModelResultVO> response = restTemplate.postForEntity(CV_MODEL_URL, multiValueMapHttpEntity, CVModelResultVO.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.info("调用CV模型接口请求失败");
                return null;
            }

            CVModelResultVO body = response.getBody();
            if (null == body || body.getStatus() != HttpStatus.OK.value()) {
                log.error("调用 CV 模型接口异常，返回结果：{}", body);
                return null;
            }

            log.info("调用CV模型接口返回结果：{}", JSONUtil.toJsonStr(body));
            return body;
        } catch (Exception e) {
            log.error("调用CV模型接口失败：", e);
        }
        return null;
    }

    @Override
    public void analyze(AnalysisQueryVO analysisQueryVO) {
        CompletableFuture.runAsync(() -> {
            try {
                String base64Image = analysisQueryVO.getImgBase64();

                // 调用 CV 模型
                CVModelResultVO cvModelResultVO = executeCVModel(base64Image);
                if (null == cvModelResultVO) {
                    throw new RuntimeException("调用 CV 模型接口异常,返回结果为空");
                }

                List<CVModelResultVO.DataDTO> dataList = cvModelResultVO.getData();
                StringBuilder labels = buildLabels(dataList);

                // 3.根据 cv 模型返回的结果 调用 大模型的接口
                String text = "提示词：##任务：依据所附带的人物照片，人物标签，输出要求，按照图片红框框出来的人物信息的红色数字编号信息，分别归纳出一句对人物的祝福话语，需体现积极、正面且具有褒义的特点 ##限制：禁止输出含有负面意义的表达 ##人物标签：%s  ##输出要求：按照顺序分别输出，不要包含特殊字符，按照中文结果输出";
                String format = String.format(text, labels);
                BigModelQueryVO modelQueryVO = init(format, base64Image);
                BigModelAnalysisVO bigModelAnalysisVO = executeBigModel(modelQueryVO);

                // 4.组装数据
                AnalysisVO analysisVO = buildResult(analysisQueryVO, bigModelAnalysisVO, dataList);
                cacheMap.put(analysisQueryVO.getImgName(), ResultVO.success(analysisVO));
            } catch (Exception e) {
                log.error("执行模型分析任务失败：", e);
                cacheMap.put(analysisQueryVO.getImgName(), buildError());
            }
        });
    }

    private static AnalysisVO buildResult(AnalysisQueryVO analysisQueryVO, BigModelAnalysisVO bigModelAnalysisVO, List<CVModelResultVO.DataDTO> dataList) {
        List<AnalysisVO.UsersDTO> usersDTOList = new ArrayList<>();

        List<BigModelAnalysisVO.ChoicesDTO> choices = bigModelAnalysisVO.getChoices();
        if (CollUtil.isEmpty(choices)) {
            log.warn("图片：{}，调用大模型获取不到具体的解析结果", analysisQueryVO.getImgName());
            return null;
        }

        for (BigModelAnalysisVO.ChoicesDTO choice : choices) {
            String content = choice.getMessage().getContent();
            // 防止数组下标越界，直接初始化
            String[] contentArr = new String[dataList.size()];
            if (StrUtil.isNotBlank(content)) {
                String[] curContentArr = content.split("\n");
                contentArr = Arrays.copyOfRange(curContentArr, 0, curContentArr.length);
            }

            if (CollUtil.isNotEmpty(dataList)) {
                for (int i = 0; i < dataList.size(); i++) {
                    AnalysisVO.UsersDTO usersDTO = new AnalysisVO.UsersDTO();
                    List<AnalysisVO.UsersDTO.BoxesDTO> boxesDTOList = new ArrayList<>();
                    List<AnalysisVO.UsersDTO.LabelsDTO> labelsDTOList = new ArrayList<>();

                    // 人脸框
                    CVModelResultVO.DataDTO.BoxDTO box = dataList.get(i).getBody().getBox();
                    // 设置坐标
                    AnalysisVO.UsersDTO.BoxesDTO boxesDTO = BeanUtil.copyProperties(box, AnalysisVO.UsersDTO.BoxesDTO.class);
                    boxesDTOList.add(boxesDTO);

                    // 设置标签
                    List<CVModelResultVO.DataDTO.PropsDTO> bodyPropsDTOList = dataList.get(i).getBody().getProps();
                    List<CVModelResultVO.DataDTO.PropsDTO> facePropsDTOList = dataList.get(i).getFace().getProps();
                    List<CVModelResultVO.DataDTO.PropsDTO> alllist = getPropsDTOS(bodyPropsDTOList, facePropsDTOList);

                    alllist.forEach(o -> {
                        AnalysisVO.UsersDTO.LabelsDTO labelsDTO = new AnalysisVO.UsersDTO.LabelsDTO();
                        labelsDTO.setDes(o.getLabel());
                        labelsDTO.setLabelName(o.getLabel());
                        labelsDTOList.add(labelsDTO);
                    });

                    usersDTO.setBoxes(boxesDTOList);
                    usersDTO.setLabels(labelsDTOList);
                    usersDTO.setContent(contentArr[i]);
                    usersDTO.setId(IdUtil.getSnowflakeNextId());
                    usersDTOList.add(usersDTO);
                }
            }
        }

        AnalysisVO analysisVO = new AnalysisVO();
        analysisVO.setUsers(usersDTOList);
        return analysisVO;
    }

    private static List<CVModelResultVO.DataDTO.PropsDTO> getPropsDTOS(List<CVModelResultVO.DataDTO.PropsDTO> bodyPropsDTOList, List<CVModelResultVO.DataDTO.PropsDTO> facePropsDTOList) {
        List<CVModelResultVO.DataDTO.PropsDTO> alllist = new ArrayList<>();
        alllist.addAll(bodyPropsDTOList);
        alllist.addAll(facePropsDTOList);
        // 取人体 + 人脸 置信度 > 0.5 的标签
        alllist = alllist.stream().filter(o -> o.getConfidence() > 0.5).sorted(Comparator.comparing(CVModelResultVO.DataDTO.PropsDTO::getConfidence).reversed()).collect(Collectors.toList());
        return alllist;
    }

    private static StringBuilder buildLabels(List<CVModelResultVO.DataDTO> dataList) {
        StringBuilder builder = new StringBuilder();
        if (CollUtil.isNotEmpty(dataList)) {
            for (int i = 0; i < dataList.size(); i++) {
                // 设置标签
                List<CVModelResultVO.DataDTO.PropsDTO> bodyPropsDTOList = dataList.get(i).getBody().getProps();
                List<CVModelResultVO.DataDTO.PropsDTO> facePropsDTOList = dataList.get(i).getFace().getProps();
                List<CVModelResultVO.DataDTO.PropsDTO> alllist = getPropsDTOS(bodyPropsDTOList, facePropsDTOList);

                // 组装标签信息
                String label = alllist.stream().map(CVModelResultVO.DataDTO.PropsDTO::getLabel).collect(Collectors.joining(","));
                builder.append(i).append("号人物").append("：");
                builder.append(label).append("  ");
            }
        }
        return builder;
    }

    @Override
    public ResultVO<AnalysisVO> result(String imgName) {
        if (cacheMap.containsKey(imgName)) {
            return cacheMap.get(imgName);
        }

        return buildNotFinish();
    }

    private BigModelQueryVO init(String des, String imgUrl) {
        BigModelQueryVO bigModelQueryVO = new BigModelQueryVO();
        bigModelQueryVO.setTemperature(0.1);
        bigModelQueryVO.setModel("uranmm-40B");
        bigModelQueryVO.setMax_tokens(512);
        bigModelQueryVO.setPresence_penalty(1.5);

        BigModelQueryVO.MessagesDTO messagesDTO = new BigModelQueryVO.MessagesDTO();
        messagesDTO.setRole("user");

        List<BigModelQueryVO.MessagesDTO.ContentDTO> contentDTOList = new ArrayList<>();
        contentDTOList.add(BigModelQueryVO.MessagesDTO.ContentDTO.builder().type("text").text(des).build());

        contentDTOList.add(BigModelQueryVO.MessagesDTO.ContentDTO.builder().type("image_url").image_url(BigModelQueryVO.MessagesDTO.ContentDTO.ImageUrlDTO.builder().url(imgUrl).build()).build());

        messagesDTO.setContent(contentDTOList);
        bigModelQueryVO.setMessages(Collections.singletonList(messagesDTO));
        return bigModelQueryVO;
    }

    private static ResultVO<AnalysisVO> buildNotFinish() {
        return ResultVO.failure(700, "任务执行中，请稍后");
    }

    private static ResultVO<AnalysisVO> buildError() {
        return ResultVO.failure(500, "获取模型分析接口异常");
    }
}
