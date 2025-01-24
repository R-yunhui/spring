package com.ral.young.spring.demo.converter.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class ConversionUtils {

    public static final String DEFAULT_DEPTH = "3";
    public static final String DEFAULT_SEGMENTED = "0";
    public static final String DEFAULT_POSE = "Unspecified";
    public static final String DEFAULT_TRUNCATED = "0";
    public static final String DEFAULT_DIFFICULT = "0";

    /**
     * 创建边界框的四个点
     */
    public JSONArray createBoundingBoxPoints(int xmin, int ymin, int xmax, int ymax) {
        JSONArray points = new JSONArray();
        points.add(createPoint(xmin, ymin)); // 左上
        points.add(createPoint(xmax, ymin)); // 右上
        points.add(createPoint(xmin, ymax)); // 左下
        points.add(createPoint(xmax, ymax)); // 右下
        return points;
    }

    /**
     * 创建单个坐标点
     */
    public JSONObject createPoint(int x, int y) {
        JSONObject point = new JSONObject();
        point.set("x", x);
        point.set("y", y);
        return point;
    }

    /**
     * 获取标签代码
     */
    public String getLabelCode(String labelName, Map<String, String> mapping) {
        if (mapping != null && mapping.containsKey(labelName)) {
            return mapping.get(labelName);
        }
        return "小汽车".equals(labelName) ? "car" : labelName.toLowerCase();
    }

    /**
     * 从边界框点获取坐标
     */
    public int[] getBoundingBoxCoordinates(JSONArray points) {
        if (points.size() >= 4) {
            JSONObject leftTop = points.getJSONObject(0);
            JSONObject rightTop = points.getJSONObject(1);
            JSONObject leftBottom = points.getJSONObject(2);
            
            return new int[] {
                leftTop.getInt("x"),    // xmin
                leftTop.getInt("y"),    // ymin
                rightTop.getInt("x"),   // xmax
                leftBottom.getInt("y")  // ymax
            };
        }
        return null;
    }

    /**
     * 创建文件信息对象
     */
    public JSONObject createFileInfo(String filename, String width, String height) {
        JSONObject fileInfo = new JSONObject();
        fileInfo.set("name", filename);
        fileInfo.set("width", width);
        fileInfo.set("height", height);
        return fileInfo;
    }
} 