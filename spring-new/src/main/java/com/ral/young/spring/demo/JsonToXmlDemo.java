package com.ral.young.spring.demo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author renyunhui
 * @description 这是一个 json 和 xml相互转换的 demo
 * @date 2025-01-23 18-14-17
 * @since 1.0.0
 */
@Slf4j
public class JsonToXmlDemo {

    public static void main(String[] args) {
        String json = FileUtil.readString(new File("C:\\Users\\Administrator\\Desktop\\学习中心v1.5\\已标注样例数据\\person_car_100114.json"), Charset.defaultCharset());
        String xmlRes = jsonToXml(json);
        log.info("xmlRes:\n{}", xmlRes);

        String xml = FileUtil.readString(new File("C:\\Users\\Administrator\\Desktop\\学习中心v1.5\\0ad107991cb34037b2f9886d86c258fa.xml"), Charset.defaultCharset());
        String jsonRes = xmlToJson(xml);
        log.info("jsonRes:\n{}", jsonRes);
    }


    /**
     * JSON转XML格式
     */
    public static String jsonToXml(String jsonStr) {
        try {
            JSONObject json = JSONUtil.parseObj(jsonStr);
            Document doc = XmlUtil.createXml();
            Element root = doc.createElement("annotation");
            doc.appendChild(root);

            // 处理文件信息
            JSONObject fileInfo = json.getJSONObject("file");
            createElement(doc, root, "folder", "images");
            createElement(doc, root, "filename", fileInfo.getStr("name"));

            // 处理默认 source
            Element source = createElement(doc, root, "source", null);
            createElement(doc, source, "database", "Unknown");

            // 处理size信息
            Element size = createElement(doc, root, "size", null);
            createElement(doc, size, "width", fileInfo.getStr("width"));
            createElement(doc, size, "height", fileInfo.getStr("height"));
            createElement(doc, size, "depth", "3");

            // 默认增加 segmented
            createElement(doc, root, "segmented", "0");

            // 处理标注信息
            JSONArray annotations = json.getJSONArray("annotation");
            for (int i = 0; i < annotations.size(); i++) {
                JSONObject anno = annotations.getJSONObject(i);
                Element object = createElement(doc, root, "object", null);

                createElement(doc, object, "name", anno.getStr("labelName"));
                createElement(doc, object, "pose", "Unspecified");
                createElement(doc, object, "truncated", "0");
                createElement(doc, object, "difficult", "0");

                // 处理边界框
                JSONArray points = anno.getJSONArray("points");
                Element bndbox = createElement(doc, object, "bndbox", null);

                JSONObject leftTop = points.getJSONObject(0);
                JSONObject rightTop = points.getJSONObject(1);
                JSONObject leftBottom = points.getJSONObject(2);

                createElement(doc, bndbox, "xmin", leftTop.getStr("x"));
                createElement(doc, bndbox, "ymin", leftTop.getStr("y"));
                createElement(doc, bndbox, "xmax", rightTop.getStr("x"));
                createElement(doc, bndbox, "ymax", leftBottom.getStr("y"));
            }

            return XmlUtil.toStr(doc, true);
        } catch (Exception e) {
            log.error("JSON转XML失败", e);
            throw new RuntimeException("JSON转XML失败", e);
        }
    }

    /**
     * XML转JSON格式
     */
    public static String xmlToJson(String xmlStr) {
        try {
            Document doc = XmlUtil.parseXml(xmlStr);
            JSONObject json = new JSONObject();

            // 构建file信息
            JSONObject fileInfo = new JSONObject();
            fileInfo.set("name", getElementText(doc, "filename"));

            // 获取size元素
            Element sizeElement = (Element) doc.getElementsByTagName("size").item(0);
            if (sizeElement != null) {
                fileInfo.set("width", Integer.parseInt(getElementTextContent(sizeElement, "width")));
                fileInfo.set("height", Integer.parseInt(getElementTextContent(sizeElement, "height")));
            }
            json.set("file", fileInfo);

            // 构建annotation数组
            JSONArray annotations = new JSONArray();
            NodeList objects = doc.getElementsByTagName("object");

            for (int i = 0; i < objects.getLength(); i++) {
                Element object = (Element) objects.item(i);
                JSONObject annotation = new JSONObject();

                String labelName = getElementTextContent(object, "name");
                annotation.set("labelName", labelName);
                annotation.set("labelCode", getLabelCode(labelName));

                // 处理边界框坐标
                Element bndbox = (Element) object.getElementsByTagName("bndbox").item(0);
                if (bndbox != null) {
                    int xmin = Integer.parseInt(getElementTextContent(bndbox, "xmin"));
                    int ymin = Integer.parseInt(getElementTextContent(bndbox, "ymin"));
                    int xmax = Integer.parseInt(getElementTextContent(bndbox, "xmax"));
                    int ymax = Integer.parseInt(getElementTextContent(bndbox, "ymax"));

                    // 构建四个角点
                    JSONArray points = new JSONArray();
                    points.add(createPoint(xmin, ymin)); // 左上
                    points.add(createPoint(xmax, ymin)); // 右上
                    points.add(createPoint(xmin, ymax)); // 左下
                    points.add(createPoint(xmax, ymax)); // 右下

                    annotation.set("points", points);
                }
                annotations.add(annotation);
            }

            json.set("annotation", annotations);
            return json.toStringPretty();
        } catch (Exception e) {
            log.error("XML转JSON失败", e);
            throw new RuntimeException("XML转JSON失败", e);
        }
    }

    /**
     * 创建XML元素
     */
    private static Element createElement(Document doc, Element parent, String tagName, String content) {
        Element element = doc.createElement(tagName);
        if (content != null) {
            element.setTextContent(content);
        }
        parent.appendChild(element);
        return element;
    }

    /**
     * 获取元素文本内容
     */
    private static String getElementText(Document doc, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return StrUtil.EMPTY;
    }

    /**
     * 获取子元素文本内容
     */
    private static String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return StrUtil.EMPTY;
    }

    /**
     * 创建坐标点JSON对象
     */
    private static JSONObject createPoint(int x, int y) {
        JSONObject point = new JSONObject();
        point.set("x", x);
        point.set("y", y);
        return point;
    }

    /**
     * todo xml中不包含对应的code，这里先进行写死
     * 获取标签代码
     */
    private static String getLabelCode(String labelName) {
        return "小汽车".equals(labelName) ? "car" : labelName.toLowerCase();
    }
}
