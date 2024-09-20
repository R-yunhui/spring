package com.ral.young.controller;

import com.ral.young.service.ResourceAlarmMessageService;
import com.ral.young.service.ResourceAlarmRuleService;
import com.ral.young.vo.ResourceAlarmMessageVO;
import com.ral.young.vo.ResourceAlarmRuleVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author renyunhui
 * @description 这是一个ResourceAlarmController类
 * @date 2024-09-19 17-56-07
 * @since 1.2.0
 */
@RestController
public class ResourceAlarmController {

    @Resource
    private ResourceAlarmMessageService resourceAlarmMessageService;

    @Resource
    private ResourceAlarmRuleService resourceAlarmRuleService;

    @GetMapping("/api/v1/resource-alarm/queryResourceAlarmRule")
    public List<ResourceAlarmRuleVO> queryResourceAlarmRule() {
        return resourceAlarmRuleService.queryResourceAlarmRule();
    }

    @PostMapping("/api/v1/resource-alarm/saveOrUpdateResourceAlarmRule")
    public void saveOrUpdateResourceAlarmRule(@RequestBody List<ResourceAlarmRuleVO> resourceAlarmRuleVOS) {
        resourceAlarmRuleService.saveOrUpdateResourceAlarmRule(resourceAlarmRuleVOS);
    }

    @GetMapping("/api/v1/resource-alarm/queryResourceAlarmMessage")
    public List<ResourceAlarmMessageVO> queryResourceAlarmMessage() {
        return resourceAlarmMessageService.queryResourceAlarmMessage();
    }

    @PostMapping("/api/v1/resource-alarm/deleteResourceAlarmMessage")
    public Boolean deleteResourceAlarmMessage(@RequestBody List<Long> ids) {
        return resourceAlarmMessageService.deleteResourceAlarmMessage(ids);
    }
}
