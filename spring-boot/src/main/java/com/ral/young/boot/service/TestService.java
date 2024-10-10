package com.ral.young.boot.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @date 2024-08-12 17:04
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestService implements ApplicationRunner {

    private boolean taskOneEnable = true;

    private boolean taskTwoEnable = true;

    private boolean taskThreeEnable = true;

    private boolean taskFourEnable = true;

    private String token = "hXvDT4opshAK0WV6B-bA1pQr5KFRopuI6U0n4G9I";

    @Resource
    private RestTemplate restTemplate;

    private static final String TASK_ID = "taskId";

    private static final String TS = "ts";

    private static final String EVENT_ID = "eventID";

    private static final Integer SUCCESS_CODE = 200;

    private static final String STATUS = "status";

    @Scheduled(fixedRateString = "${scheduled.fixedRateTime}", initialDelayString = "${scheduled.initialDelayTimeOne}")
    public void testSendAlarmDataOne() {
        if (taskOneEnable) {
            try {
                // String data = "{\"ts\":1723514795220,\"tags\":{\"presetId\":\"-1\",\"severity\":\"\\\"GENERAL\\\"\",\"twiceEnlargeNumber\":\"2.0\",\"twiceReviewInterval\":\"30.0\",\"twiceRecognizeThreshold\":\"60.0\"},\"zoom\":2,\"boxes\":[{\"x\":838,\"y\":369,\"width\":194,\"height\":638},{\"x\":1006,\"y\":453,\"width\":217,\"height\":81}],\"scene\":\"/group1/alarm/20240808/15/54/8/bcd48589408b89a0d87fc704a23086dd.png?download=0\",\"score\":0.326904296875,\"taskId\":\"1777612807331780001\",\"channel\":{\"height\":5.0,\"eastPan\":0.0,\"orgCode\":\"1774690368726835201_1777186040751124482\",\"latitude\":30.63238635,\"northPan\":0.0,\"tenantId\":1774690367569207297,\"longitude\":104.1804775,\"pitchAngle\":0.0,\"channelName\":\"不按规定车道行驶测试设备_0\",\"xCoordinate\":0.0,\"yCoordinate\":0.0,\"zCoordinate\":0.0,\"dueNorthAngle\":0.0,\"verticalFieldOfView\":0.0,\"horizontalFieldOfView\":0.0},\"eventID\":\"5267cb94f64a11eea43ba62fef743ab6\",\"cameraId\":\"1823268253452546049\",\"alarmType\":\"buanguidaochedao\",\"taskParam\":{\"presetId\":\"-1\",\"severity\":\"\\\"GENERAL\\\"\",\"twiceEnlargeNumber\":\"2.0\",\"twiceReviewInterval\":\"30.0\",\"twiceRecognizeThreshold\":\"60.0\"},\"normalType\":false,\"sceneWidth\":1920,\"sceneHeight\":1080,\"abilityParams\":{\"id\":\"bc8ee1820e852d0b3831bd280bbd1b97\",\"minBox\":{\"width\":25,\"height\":25},\"interval\":2,\"minTarry\":30,\"areaBoxes\":[],\"threshold\":0.25,\"mergeEnable\":false,\"objMinCount\":1,\"periodTimes\":[{\"endTime\":\"09:00:00\",\"startTime\":\"18:00:00\"}],\"shieldAreas\":[],\"analysisMode\":\"VIDEO_STREAM\",\"alarmInterval\":60},\"alarmVideoStop\":1563368095747,\"alarmVideoStart\":256,\"twiceEnlargeNumber\":\"2.0\",\"twiceRecognizeThreshold\":\"60.0\"}";
                String data = "{\"boxes\":[{\"x\":838,\"y\":369,\"width\":194,\"height\":638},{\"x\":1006,\"y\":453,\"width\":217,\"height\":81}],\"eventID\":\"a844c5cc6a7f11ef81d23aff5d25b14d\",\"sceneHeight\":720,\"normalType\":false,\"channel\":{\"eastPan\":0.0,\"latitude\":0.0,\"northPan\":0.0,\"pitchAngle\":0.0,\"xCoordinate\":49.75547791,\"horizontalFieldOfView\":0.0,\"yCoordinate\":49.73924381,\"orgCode\":\"1843573406130229250\",\"tenantId\":\"1843573405983428610\",\"zCoordinate\":0.0,\"channelName\":\"不按规定车道行驶2\",\"verticalFieldOfView\":0.0,\"dueNorthAngle\":0.0,\"longitude\":0.0,\"height\":1.0},\"zoom\":0,\"alarmVideoStop\":0,\"scene\":\"/group1/alarm/20241009/15/24/3/buanguidingchedaoxingshi\",\"tags\":{\"presetId\":\"-1\",\"severity\":\"GENERAL\"},\"score\":0.83984375,\"alarmType\":\"WorkingWithHelmetAndHook\",\"cameraId\":\"1843826510181392386\",\"alarmVideoStart\":\"4737558152524857344\",\"extra\":{\"itemsInBox\":[{\"confidence\":0.83984375,\"type\":\"tanker_side\"}]},\"sceneWidth\":1280,\"alarmEventStatus\":1,\"abilityParams\":{\"periodTimes\":[{\"startTime\":\"09:00\",\"endTime\":\"18:00\"}],\"shieldAreas\":[],\"analysisMode\":\"VIDEO_STREAM\",\"areaBoxes\":[],\"id\":\"1843838295293882369\"},\"ability\":\"不按规定车道行驶\",\"taskId\":\"1830896934910971906\",\"ts\":\"1725428198823\"}";
                Result result = getResult(data);

                ResponseEntity<JSONObject> response = getResponse(result);
                JSONObject body = response.getBody();
                if (SUCCESS_CODE.equals(body.getInt(STATUS))) {
                    log.info("【不按规定车道行驶测试告警数据】 推送成功，发送时间：{}，taskId：{} , eventId：{} ，resp：{}", DateUtil.now(), result.taskId, result.eventId, body);
                } else {
                    log.error("【不按规定车道行驶测试告警数据】 推送失败，发送时间：{}，taskId：{} , eventId：{} ，resp：{}", DateUtil.now(), result.taskId, result.eventId, body);
                }
            } catch (Exception e) {
                log.error("【不按规定车道行驶测试告警数据】 推送失败，errorMsg：", e);
            }
        }
    }

    public void testSendAlarmDataFive() {
        try {
            for (int i = 0; i < 30; i ++) {
                String data = "{\"boxes\":[{\"height\":42,\"width\":90,\"x\":1066,\"y\":157}],\"eventID\":\"a844c5cc6a7f11ef81d23aff5d25b14d\",\"sceneHeight\":720,\"normalType\":false,\"channel\":{\"eastPan\":0.0,\"latitude\":0.0,\"northPan\":0.0,\"pitchAngle\":0.0,\"xCoordinate\":49.75547791,\"horizontalFieldOfView\":0.0,\"yCoordinate\":49.73924381,\"orgCode\":\"1830585359525699585\",\"tenantId\":\"1830585359127240705\",\"zCoordinate\":0.0,\"channelName\":\"IP PTZ Camera\",\"verticalFieldOfView\":0.0,\"dueNorthAngle\":1.0,\"longitude\":0.0,\"height\":1.0},\"zoom\":0,\"alarmVideoStop\":0,\"scene\":\"/group1/alarm/20240808/15/54/8/bcd48589408b89a0d87fc704a23086dd.png?download=0\",\"score\":0.83984375,\"alarmType\":\"PersonCountSnap\",\"cameraId\":\"1830846804224794626\",\"alarmVideoStart\":\"4737558152524857344\",\"extra\":{\"itemsInBox\":[{\"confidence\":0.83984375,\"type\":\"tanker_side\"}]},\"sceneWidth\":1280,\"alarmEventStatus\":1,\"abilityParams\":{\"periodTimes\":[{\"startTime\":\"00:00\",\"endTime\":\"23:59\"}],\"shieldAreas\":[],\"analysisMode\":\"VIDEO_STREAM\",\"areaBoxes\":[],\"id\":\"1830585653932556291\"},\"ability\":\"人员计数\",\"taskId\":\"1830896934910971906\",\"ts\":\"1725428198823\"}";
                Result result = getResult(data);

                ResponseEntity<JSONObject> response = getResponse(result);
                JSONObject body = response.getBody();
                if (SUCCESS_CODE.equals(body.getInt(STATUS))) {
                    log.info("测试 推送成功，发送时间：{}，taskId：{} , eventId：{} ，resp：{}", DateUtil.now(), result.taskId, result.eventId, body);
                } else {
                    log.error("测试 推送失败，发送时间：{}，taskId：{} , eventId：{} ，resp：{}", DateUtil.now(), result.taskId, result.eventId, body);
                }
            }
        } catch (Exception e) {
            log.error("测试 推送失败，errorMsg：", e);
        }
    }

    @Scheduled(fixedRateString = "${scheduled.fixedRateTime}", initialDelayString = "${scheduled.initialDelayTimeTwo}")
    public void testSendAlarmDataTwo() {
        if (taskTwoEnable) {
            try {
                // String data = "{\"ts\":1723448884261,\"tags\":{\"presetId\":\"-1\",\"severity\":\"\\\"GENERAL\\\"\",\"twiceEnlargeNumber\":\"2.0\",\"twiceReviewInterval\":\"30.0\",\"twiceRecognizeThreshold\":\"60.0\"},\"zoom\":2,\"boxes\":[{\"x\":89,\"y\":86,\"width\":37,\"height\":75},{\"x\":213,\"y\":66,\"width\":37,\"height\":69},{\"x\":167,\"y\":110,\"width\":455,\"height\":132}],\"scene\":\"/group1/alarm/20240808/15/54/8/d3395ec50ff0c782310feb2ff3b8562d.png?download=0\",\"score\":0.326904296875,\"taskId\":\"1777612807331780020\",\"channel\":{\"height\":5.0,\"eastPan\":0.0,\"orgCode\":\"1774690368726835201_1777186040751124482\",\"latitude\":30.63238635,\"northPan\":0.0,\"tenantId\":1774690367569207297,\"longitude\":104.1804775,\"pitchAngle\":0.0,\"channelName\":\"人行道未减速测试设备_0\",\"xCoordinate\":0.0,\"yCoordinate\":0.0,\"zCoordinate\":0.0,\"dueNorthAngle\":0.0,\"verticalFieldOfView\":0.0,\"horizontalFieldOfView\":0.0},\"eventID\":\"5267cb94f64a11eea43ba62fef743ef0\",\"cameraId\":\"1823529976206012417\",\"alarmType\":\"renxindaoweijiansu\",\"taskParam\":{\"presetId\":\"-1\",\"severity\":\"\\\"GENERAL\\\"\",\"twiceEnlargeNumber\":\"2.0\",\"twiceReviewInterval\":\"30.0\",\"twiceRecognizeThreshold\":\"60.0\"},\"normalType\":false,\"sceneWidth\":1920,\"sceneHeight\":1080,\"abilityParams\":{\"id\":\"5800c0a0d712b8200d8e7023d1c44535\",\"minBox\":{\"width\":25,\"height\":25},\"interval\":2,\"minTarry\":30,\"areaBoxes\":[],\"threshold\":0.25,\"mergeEnable\":false,\"objMinCount\":1,\"periodTimes\":[{\"endTime\":\"09:00:00\",\"startTime\":\"18:00:00\"}],\"shieldAreas\":[],\"analysisMode\":\"VIDEO_STREAM\",\"alarmInterval\":60},\"alarmVideoStop\":1563368095747,\"alarmVideoStart\":256,\"twiceEnlargeNumber\":\"2.0\",\"twiceRecognizeThreshold\":\"60.0\"}";
                String data = "{\"boxes\":[{\"x\":89,\"y\":86,\"width\":37,\"height\":75},{\"x\":213,\"y\":66,\"width\":37,\"height\":69},{\"x\":167,\"y\":110,\"width\":455,\"height\":132}],\"eventID\":\"a844c5cc6a7f11ef81d23aff5d25b14d\",\"sceneHeight\":720,\"normalType\":false,\"channel\":{\"eastPan\":0.0,\"latitude\":-3.096628,\"northPan\":0.0,\"pitchAngle\":0.0,\"xCoordinate\":49.75547791,\"horizontalFieldOfView\":0.0,\"yCoordinate\":49.73924381,\"orgCode\":\"1843573406130229250\",\"tenantId\":\"1843573405983428610\",\"zCoordinate\":0.0,\"channelName\":\"人行道车减速\",\"verticalFieldOfView\":0.0,\"dueNorthAngle\":20.0,\"longitude\":2.815242,\"height\":3.0},\"zoom\":0,\"alarmVideoStop\":0,\"scene\":\"/group1/alarm/20241009/14/39/3/renxingdaoweijiansu\",\"tags\":{\"presetId\":\"-1\",\"severity\":\"GENERAL\"},\"score\":0.83984375,\"alarmType\":\"VerticalClothCartMove\",\"cameraId\":\"1843826205918191617\",\"alarmVideoStart\":\"4737558152524857344\",\"extra\":{\"itemsInBox\":[{\"confidence\":0.83984375,\"type\":\"tanker_side\"}]},\"sceneWidth\":1280,\"alarmEventStatus\":1,\"abilityParams\":{\"periodTimes\":[{\"startTime\":\"09:00\",\"endTime\":\"18:00\"}],\"shieldAreas\":[],\"analysisMode\":\"VIDEO_STREAM\",\"areaBoxes\":[],\"id\":\"1843838295293882369\"},\"ability\":\"人行道车减速\",\"taskId\":\"1830896934910971906\",\"ts\":\"1725428198823\"}";
                Result result = getResult(data);

                ResponseEntity<JSONObject> response = getResponse(result);
                JSONObject body = response.getBody();
                if (SUCCESS_CODE.equals(body.getInt(STATUS))) {
                    log.info("【人行道未减速测试告警数据】 推送成功，发送时间：{}，taskId：{} , eventId：{} ，resp：{}", DateUtil.now(), result.taskId, result.eventId, body);
                } else {
                    log.error("【人行道未减速测试告警数据】 推送失败，发送时间：{}，taskId：{} , eventId：{} ，resp：{}", DateUtil.now(), result.taskId, result.eventId, body);
                }
            } catch (Exception e) {
                log.error("【人行道未减速测试告警数据】 推送失败，errorMsg：", e);
            }
        }
    }

    @Scheduled(fixedRateString = "${scheduled.fixedRateTime}", initialDelayString = "${scheduled.initialDelayTimeThree}")
    public void testSendAlarmDataThree() {
        if (taskThreeEnable) {
            try {
                // String data = "{\"ts\":1723514795000,\"tags\":{\"presetId\":\"-1\",\"severity\":\"\\\"GENERAL\\\"\",\"twiceEnlargeNumber\":\"2.0\",\"twiceReviewInterval\":\"30.0\",\"twiceRecognizeThreshold\":\"60.0\"},\"zoom\":2,\"boxes\":[{\"x\":824,\"y\":608,\"width\":74,\"height\":209},{\"x\":672,\"y\":688,\"width\":76,\"height\":81}],\"scene\":\"/group1/alarm/20240808/15/54/8/7de3ecaad36f6639185a89e7d7d8f9aa.png?download=0\",\"score\":0.326904296875,\"taskId\":\"1777612807331780010\",\"channel\":{\"height\":5.0,\"eastPan\":0.0,\"orgCode\":\"1774690368726835201_1777186040751124482\",\"latitude\":30.63238635,\"northPan\":0.0,\"tenantId\":1774690367569207297,\"longitude\":104.1804775,\"pitchAngle\":0.0,\"channelName\":\"车辆违规变道测试设备_0\",\"xCoordinate\":0.0,\"yCoordinate\":0.0,\"zCoordinate\":0.0,\"dueNorthAngle\":0.0,\"verticalFieldOfView\":0.0,\"horizontalFieldOfView\":0.0},\"eventID\":\"5267cb94f64a11eea43ba62fef743cd0\",\"cameraId\":\"1823529976176652290\",\"alarmType\":\"weiguibiandao\",\"taskParam\":{\"presetId\":\"-1\",\"severity\":\"\\\"GENERAL\\\"\",\"twiceEnlargeNumber\":\"2.0\",\"twiceReviewInterval\":\"30.0\",\"twiceRecognizeThreshold\":\"60.0\"},\"normalType\":false,\"sceneWidth\":1920,\"sceneHeight\":1080,\"abilityParams\":{\"id\":\"95864b452ca1f479d72f4b7c31c638ff\",\"minBox\":{\"width\":25,\"height\":25},\"interval\":2,\"minTarry\":30,\"areaBoxes\":[],\"threshold\":0.25,\"mergeEnable\":false,\"objMinCount\":1,\"periodTimes\":[{\"endTime\":\"09:00:00\",\"startTime\":\"18:00:00\"}],\"shieldAreas\":[],\"analysisMode\":\"VIDEO_STREAM\",\"alarmInterval\":60},\"alarmVideoStop\":1563368095747,\"alarmVideoStart\":256,\"twiceEnlargeNumber\":\"2.0\",\"twiceRecognizeThreshold\":\"60.0\"}";
                String data = "{\"boxes\":[{\"x\":824,\"y\":608,\"width\":74,\"height\":209},{\"x\":672,\"y\":688,\"width\":76,\"height\":81}],\"eventID\":\"a844c5cc6a7f11ef81d23aff5d25b14d\",\"sceneHeight\":720,\"normalType\":false,\"channel\":{\"eastPan\":0.0,\"latitude\":0.0,\"northPan\":0.0,\"pitchAngle\":0.0,\"xCoordinate\":49.75547791,\"horizontalFieldOfView\":0.0,\"yCoordinate\":49.73924381,\"orgCode\":\"1843573406130229250\",\"tenantId\":\"1843573405983428610\",\"zCoordinate\":0.0,\"channelName\":\"不按规定车道行驶\",\"verticalFieldOfView\":0.0,\"dueNorthAngle\":0.0,\"longitude\":0.0,\"height\":1.0},\"zoom\":0,\"alarmVideoStop\":0,\"scene\":\"/group1/alarm/20241009/15/30/3/cheliangweiguibiandao\",\"tags\":{\"presetId\":\"-1\",\"severity\":\"GENERAL\"},\"score\":0.83984375,\"alarmType\":\"WithoutProtectiveMask\",\"cameraId\":\"1843826418342912002\",\"alarmVideoStart\":\"4737558152524857344\",\"extra\":{\"itemsInBox\":[{\"confidence\":0.83984375,\"type\":\"tanker_side\"}]},\"sceneWidth\":1280,\"alarmEventStatus\":1,\"abilityParams\":{\"periodTimes\":[{\"startTime\":\"09:00\",\"endTime\":\"18:00\"}],\"shieldAreas\":[],\"analysisMode\":\"VIDEO_STREAM\",\"areaBoxes\":[],\"id\":\"1843838295293882369\"},\"ability\":\"车辆违规变道\",\"taskId\":\"1830896934910971906\",\"ts\":\"1725428198823\"}";
                Result result = getResult(data);

                ResponseEntity<JSONObject> response = getResponse(result);
                JSONObject body = response.getBody();
                if (SUCCESS_CODE.equals(body.getInt(STATUS))) {
                    log.info("【车辆违规变道测试告警数据】 推送成功，发送时间：{}，taskId：{} , eventId：{} ，resp：{}", DateUtil.now(), result.taskId, result.eventId, body);
                } else {
                    log.error("【车辆违规变道测试告警数据】 推送失败，发送时间：{}，taskId：{} , eventId：{} ，resp：{}", DateUtil.now(), result.taskId, result.eventId, body);
                }
            } catch (Exception e) {
                log.error("【车辆违规变道测试告警数据】 推送失败，errorMsg：", e);
            }
        }
    }

    @Scheduled(fixedRateString = "${scheduled.fixedRateTime}", initialDelayString = "${scheduled.initialDelayTimeFour}")
    public void testSendAlarmDataFour() {
        if (taskFourEnable) {
            try {
                // String data = "{\"ts\":1723448884261,\"tags\":{\"presetId\":\"-1\",\"severity\":\"\\\"GENERAL\\\"\",\"twiceEnlargeNumber\":\"2.0\",\"twiceReviewInterval\":\"30.0\",\"twiceRecognizeThreshold\":\"60.0\"},\"zoom\":2,\"boxes\":[{\"x\":622,\"y\":200,\"width\":15,\"height\":38}],\"scene\":\"/group1/alarm/20240813/15/48/8/8237df9a245329e2db3347a2a15ac414.png?download=0\",\"score\":0.326904296875,\"taskId\":\"1777612807331780020\",\"channel\":{\"height\":5.0,\"eastPan\":0.0,\"orgCode\":\"1774690368726835201_1777186040751124482\",\"latitude\":30.63238635,\"northPan\":0.0,\"tenantId\":1774690367569207297,\"longitude\":104.1804775,\"pitchAngle\":0.0,\"channelName\":\"冲抢信号灯测试设备_0\",\"xCoordinate\":0.0,\"yCoordinate\":0.0,\"zCoordinate\":0.0,\"dueNorthAngle\":0.0,\"verticalFieldOfView\":0.0,\"horizontalFieldOfView\":0.0},\"eventID\":\"5267cb94f64a11eea43ba62fef743ef0\",\"cameraId\":\"1823283355232841729\",\"alarmType\":\"chongqiangxinghaodeng\",\"taskParam\":{\"presetId\":\"-1\",\"severity\":\"\\\"GENERAL\\\"\",\"twiceEnlargeNumber\":\"2.0\",\"twiceReviewInterval\":\"30.0\",\"twiceRecognizeThreshold\":\"60.0\"},\"normalType\":false,\"sceneWidth\":1920,\"sceneHeight\":1080,\"abilityParams\":{\"id\":\"febc82ac7cf7f4ba50f3075c3b963f4c\",\"minBox\":{\"width\":25,\"height\":25},\"interval\":2,\"minTarry\":30,\"areaBoxes\":[],\"threshold\":0.25,\"mergeEnable\":false,\"objMinCount\":1,\"periodTimes\":[{\"endTime\":\"09:00:00\",\"startTime\":\"18:00:00\"}],\"shieldAreas\":[],\"analysisMode\":\"VIDEO_STREAM\",\"alarmInterval\":60},\"alarmVideoStop\":1563368095747,\"alarmVideoStart\":256,\"twiceEnlargeNumber\":\"2.0\",\"twiceRecognizeThreshold\":\"60.0\"}";
                String data = "{\"boxes\":[{\"x\":622,\"y\":200,\"width\":15,\"height\":38}],\"eventID\":\"a844c5cc6a7f11ef81d23aff5d25b14d\",\"sceneHeight\":720,\"normalType\":false,\"channel\":{\"eastPan\":0.0,\"latitude\":0.0,\"northPan\":0.0,\"pitchAngle\":0.0,\"xCoordinate\":49.75547791,\"horizontalFieldOfView\":0.0,\"yCoordinate\":49.73924381,\"orgCode\":\"1843573406130229250\",\"tenantId\":\"1843573405983428610\",\"zCoordinate\":0.0,\"channelName\":\"冲抢信号灯\",\"verticalFieldOfView\":0.0,\"dueNorthAngle\":0.0,\"longitude\":0.0,\"height\":1.0},\"zoom\":0,\"alarmVideoStop\":0,\"scene\":\"/group1/alarm/20241009/15/40/3/chongqiangxinhaodeng\",\"tags\":{\"presetId\":\"-1\",\"severity\":\"GENERAL\"},\"score\":0.83984375,\"alarmType\":\"WindlassBreakMaterial\",\"cameraId\":\"1843851720012029954\",\"alarmVideoStart\":\"4737558152524857344\",\"extra\":{\"itemsInBox\":[{\"confidence\":0.83984375,\"type\":\"tanker_side\"}]},\"sceneWidth\":1280,\"alarmEventStatus\":1,\"abilityParams\":{\"periodTimes\":[{\"startTime\":\"09:00\",\"endTime\":\"18:00\"}],\"shieldAreas\":[],\"analysisMode\":\"VIDEO_STREAM\",\"areaBoxes\":[],\"id\":\"1843838295293882369\"},\"ability\":\"冲抢信号灯\",\"taskId\":\"1830896934910971906\",\"ts\":\"1725428198823\"}";
                Result result = getResult(data);

                ResponseEntity<JSONObject> response = getResponse(result);
                JSONObject body = response.getBody();
                if (SUCCESS_CODE.equals(body.getInt(STATUS))) {
                    log.info("【冲抢信号灯测试告警数据】 推送成功，发送时间：{}，taskId：{} , eventId：{} ，resp：{}", DateUtil.now(), result.taskId, result.eventId, body);
                } else {
                    log.error("【冲抢信号灯测试告警数据】 推送失败，发送时间：{}，taskId：{} , eventId：{} ，resp：{}", DateUtil.now(), result.taskId, result.eventId, body);
                }
            } catch (Exception e) {
                log.error("【冲抢信号灯测试告警数据据】 推送失败，errorMsg：", e);
            }
        }
    }

    public String updateData(int code) {
        switch (code) {
            case 1:
                taskOneEnable = !taskOneEnable;
                break;
            case 2:
                taskTwoEnable = !taskTwoEnable;
                break;
            case 3:
                taskThreeEnable = !taskThreeEnable;
                break;
            case 4:
                taskFourEnable = !taskFourEnable;
                break;
            case 0:
                taskOneEnable = !taskOneEnable;
                taskTwoEnable = !taskTwoEnable;
                taskThreeEnable = !taskThreeEnable;
                taskFourEnable = !taskFourEnable;
                break;
            default:
                break;
        }
        String msg = "success" + "  taskOneEnable:" + taskOneEnable + "  taskTwoEnable:" + taskTwoEnable + "  " + "  taskThreeEnable:" + taskThreeEnable + "  " + "  taskFourEnable:" + taskFourEnable;
        log.info("msg：{}", msg);
        return msg;
    }

    public String updateToken(String token) {
        this.token = token;
        return token;
    }

    private ResponseEntity<JSONObject> getResponse(Result result) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-auth-token", token);

        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(result.jsonObject, headers);
        return restTemplate.postForEntity("http://182.140.132.195:32800/user-alarm/api/v1/event/receive", requestEntity, JSONObject.class);
    }

    private static Result getResult(String data) {
        JSONObject jsonObject = new JSONObject(data);
        String taskId = jsonObject.getStr(TASK_ID);
        taskId = replaceLastSix(taskId, true, 10);
        Long timeStamp = DateUtil.current();
        String eventId = jsonObject.getStr(EVENT_ID);
        eventId = replaceLastSix(eventId, false, 10);

        jsonObject.replace(TASK_ID, taskId);
        jsonObject.replace(EVENT_ID, eventId);
        jsonObject.replace(TS, timeStamp);
        return new Result(jsonObject, taskId, eventId);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        testSendAlarmDataOne();
//        testSendAlarmDataTwo();
//        testSendAlarmDataThree();
//        testSendAlarmDataFour();
    }

    private static class Result {
        public final JSONObject jsonObject;
        public final String taskId;
        public final String eventId;

        public Result(JSONObject jsonObject, String taskId, String eventId) {
            this.jsonObject = jsonObject;
            this.taskId = taskId;
            this.eventId = eventId;
        }
    }

    public static String replaceLastSix(String str, boolean number, int replaceSize) {
        if (str.length() >= replaceSize) {
            String toReplace = str.substring(str.length() - replaceSize);
            String replacement = number ? RandomUtil.randomNumbers(replaceSize) : RandomUtil.randomString(replaceSize);
            return str.replace(toReplace, replacement);
        }
        return str;
    }
}
