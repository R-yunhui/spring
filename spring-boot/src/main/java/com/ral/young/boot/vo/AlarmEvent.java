package com.ral.young.boot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author renyunhui
 * @description 这是一个AlarmEvent类
 * @date 2024-12-16 10-44-12
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmEvent {
    private int alarmEventStatus;
    private String alarmType;
    private long alarmVideoStart;
    private long alarmVideoStop;
    private List<Box> boxes;
    private Long cameraId;
    private String eventID;
    private Extra extra;
    private boolean normalType;
    private String scene;
    private int sceneHeight;
    private int sceneWidth;
    private double score;
    private long ts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Box {
        private int height;
        private int width;
        private int x;
        private int y;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Extra {
        private List<Object> auxiliaryObjs; // 根据实际类型调整
        private Object cei; // 根据实际类型调整
        private List<ItemInBox> itemsInBox;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemInBox {
        private int age;
        private List<Attribute> bodyAttrs;
        private double confidence;
        private List<Attribute> faceAttrs;
        private String sex;
        private double sexConfidence;
        private String type;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attribute {
        private int cls;
        private double confidence;
        private int index;
        private String name;
    }
}
