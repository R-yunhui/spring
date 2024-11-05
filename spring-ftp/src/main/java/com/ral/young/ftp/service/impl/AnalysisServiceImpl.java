package com.ral.young.ftp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    public static final String CV_MODEL_URL = "/statical/api/v1/body/attribute";

    public static Map<String, ResultVO<AnalysisVO>> cacheMap = new HashMap<>(8);

    @Override
    public BigModelAnalysisVO executeBigModel(BigModelQueryVO modelQueryVO) {
        try {
            // BigModelQueryVO bigModelQueryVO = init("图片中是个人吗？如果是，请回答是，并且具体描述一下这个人的长相和性别。如果不是，则回答否", "https://www.bing.com/images/search?view=detailV2&ccid=w1f2DgOu&id=D3BE43BBE8ABC1505C794EACE884AC53F82C55DC&thid=OIP.w1f2DgOu0x1m2ezNPK3aBAAAAA&mediaurl=https%3a%2f%2fimg95.699pic.com%2fphoto%2f60001%2f4095.jpg_wh300.jpg&cdnurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR.c357f60e03aed31d66d9eccd3cadda04%3frik%3d3FUs%252bFOshOisTg%26pid%3dImgRaw%26r%3d0&exph=300&expw=450&q=%e4%ba%ba%e8%84%b8%e5%9b%be%e7%89%87%0d%0a%e4%ba%ba%e8%84%b8%e5%9b%be%e7%89%87&simid=608042811646877153&FORM=IRPRST&ck=7D3BF3DFEF6CC7277456F8E52C980718&selectedIndex=108&itb=0");
            // 必须是一个 jsonObj 的参数
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
    public CVModelResultVO executeCVModel() {
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 创建请求体
        Map<String, Object> paramMap = new HashMap<>();
        // 这里的 "file" 是接口所需的图片地址
        paramMap.put("file", "");
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(paramMap, headers);
        try {
            ResponseEntity<CVModelResultVO> response = restTemplate.postForEntity(CV_MODEL_URL, requestEntity, CVModelResultVO.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.info("调用CV模型接口请求失败");
                return null;
            }

            CVModelResultVO body = response.getBody();
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
                CVModelResultVO cvModelResultVO = executeCVModel();
                List<CVModelResultVO.DataDTO> dataList = cvModelResultVO.getData();
                StringBuilder builder = new StringBuilder();
                if (CollUtil.isNotEmpty(dataList)) {
                    for (int i = 0; i < dataList.size(); i++) {
                        // 设置标签
                        List<CVModelResultVO.DataDTO.PropsDTO> bodyPropsDTOList = dataList.get(i).getBody().getProps();
                        List<CVModelResultVO.DataDTO.PropsDTO> facePropsDTOList = dataList.get(i).getFace().getProps();
                        bodyPropsDTOList.addAll(facePropsDTOList);
                        // 取人体 + 人脸 置信度 top5 的标签
                        List<CVModelResultVO.DataDTO.PropsDTO> top5Props = bodyPropsDTOList.stream()
                                .sorted(Comparator.comparing(CVModelResultVO.DataDTO.PropsDTO::getConfidence).reversed())
                                .limit(5).collect(Collectors.toList());

                        // 组装标签信息
                        String label = top5Props.stream().map(CVModelResultVO.DataDTO.PropsDTO::getLabel).collect(Collectors.joining(","));
                        builder.append(i).append("号人物").append("：");
                        builder.append(label).append("\n");
                    }
                }

                // 3.根据 cv 模型返回的结果 调用 大模型的接口
                String text = "提示词：##任务：依据所附带的人物照片，人物标签，输出要求，按照图片红框框出来的人物信息的红色数字编号信息，分别归纳出一句对人物的祝福话语，需体现积极、正面且具有褒义的特点 ##限制：禁止输出含有负面意义的表达 ##人物标签：%s  ##输出要求：按照顺序分别输出，不要包含特殊字符";
                String format = String.format(text, builder);
                BigModelQueryVO modelQueryVO = init(format, base64Image);
                BigModelAnalysisVO bigModelAnalysisVO = executeBigModel(modelQueryVO);

                // 4.组装数据
                AnalysisVO.DataDTO dataDTO = new AnalysisVO.DataDTO();
                List<AnalysisVO.DataDTO.UsersDTO> usersDTOList = new ArrayList<>();

                List<BigModelAnalysisVO.ChoicesDTO> choices = bigModelAnalysisVO.getChoices();
                if (CollUtil.isEmpty(choices)) {
                    log.warn("图片：{}，调用大模型获取不到具体的解析结果", analysisQueryVO.getImgName());
                    return;
                }

                for (BigModelAnalysisVO.ChoicesDTO choice : choices) {
                    AnalysisVO.DataDTO.UsersDTO usersDTO = new AnalysisVO.DataDTO.UsersDTO();
                    List<AnalysisVO.DataDTO.UsersDTO.BoxesDTO> boxesDTOList = new ArrayList<>();
                    List<AnalysisVO.DataDTO.UsersDTO.LabelsDTO> labelsDTOList = new ArrayList<>();

                    if (CollUtil.isNotEmpty(dataList)) {
                        for (CVModelResultVO.DataDTO data : dataList) {
                            // 人脸框
                            CVModelResultVO.DataDTO.BoxDTO box = data.getBody().getBox();
                            // 设置坐标
                            AnalysisVO.DataDTO.UsersDTO.BoxesDTO boxesDTO = BeanUtil.copyProperties(box, AnalysisVO.DataDTO.UsersDTO.BoxesDTO.class);
                            boxesDTOList.add(boxesDTO);

                            // 设置标签
                            List<CVModelResultVO.DataDTO.PropsDTO> bodyPropsDTOList = data.getBody().getProps();
                            List<CVModelResultVO.DataDTO.PropsDTO> facePropsDTOList = data.getFace().getProps();
                            bodyPropsDTOList.addAll(facePropsDTOList);
                            // 取人体 + 人脸 置信度 top5 的标签
                            List<CVModelResultVO.DataDTO.PropsDTO> top5Props = bodyPropsDTOList.stream()
                                    .sorted(Comparator.comparing(CVModelResultVO.DataDTO.PropsDTO::getConfidence).reversed())
                                    .limit(5).collect(Collectors.toList());
                            labelsDTOList.add(null);
                        }
                    }

                    usersDTO.setBoxes(boxesDTOList);
                    usersDTO.setLabels(labelsDTOList);
                    usersDTO.setContent(choice.getMessage().getContent());
                    usersDTO.setId(IdUtil.getSnowflakeNextId());
                    usersDTOList.add(usersDTO);
                }

                dataDTO.setUsers(usersDTOList);
                AnalysisVO analysisVO = new AnalysisVO();
                analysisVO.setCode("200");
                analysisVO.setMsg("success");
                analysisVO.setData(dataDTO);

                cacheMap.put(analysisQueryVO.getImgName(), ResultVO.success(analysisVO));
            } catch (Exception e) {
                log.error("执行大模型分析任务失败：", e);
                cacheMap.put(analysisQueryVO.getImgName(), buildError());
            }
        });
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
