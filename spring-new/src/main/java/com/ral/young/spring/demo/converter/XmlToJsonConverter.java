package com.ral.young.spring.demo.converter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ral.young.spring.demo.converter.util.ConversionUtils;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static com.ral.young.spring.demo.converter.ConversionMetrics.recordMetrics;

@Slf4j
public class XmlToJsonConverter implements FormatConverter {

    @Override
    public String convert(String input, ConvertConfig config) {
        try {
            long startTime = System.currentTimeMillis();
            String result = doConvert(input);
            recordMetrics("xml2json", true, System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            recordMetrics("xml2json", false, 0);
            log.error("XML转JSON失败", e);
            throw new ConversionException("XML转JSON失败", e);
        }
    }

    private String doConvert(String input) {
        Document doc = XmlUtil.parseXml(input);
        JSONObject rootJson = new JSONObject();
        
        // 处理文件信息
        String filename = getElementText(doc, "filename");
        String width = getElementText(doc, "width");
        String height = getElementText(doc, "height");
        rootJson.set("file", ConversionUtils.createFileInfo(filename, width, height));

        // 处理标注信息
        JSONArray annotations = new JSONArray();
        rootJson.set("annotation", annotations);

        NodeList objects = doc.getElementsByTagName("object");
        for (int i = 0; i < objects.getLength(); i++) {
            Element object = (Element) objects.item(i);
            JSONObject annotation = processObject(object);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }

        return rootJson.toStringPretty();
    }

    private JSONObject processObject(Element object) {
        JSONObject annotation = new JSONObject();
        
        // 处理标签名称
        String labelName = getElementTextContent(object, "name");
        if (StrUtil.isNotEmpty(labelName)) {
            annotation.set("labelName", labelName);
            annotation.set("labelCode", ConversionUtils.getLabelCode(labelName, null));
            
            // 处理边界框
            Element bndbox = (Element) object.getElementsByTagName("bndbox").item(0);
            if (bndbox != null) {
                processBoundingBox(bndbox, annotation);
            }
            
            return annotation;
        }
        
        return null;
    }

    private void processBoundingBox(Element bndbox, JSONObject annotation) {
        try {
            int xmin = Integer.parseInt(getElementTextContent(bndbox, "xmin"));
            int ymin = Integer.parseInt(getElementTextContent(bndbox, "ymin"));
            int xmax = Integer.parseInt(getElementTextContent(bndbox, "xmax"));
            int ymax = Integer.parseInt(getElementTextContent(bndbox, "ymax"));

            JSONArray points = ConversionUtils.createBoundingBoxPoints(xmin, ymin, xmax, ymax);
            annotation.set("points", points);
        } catch (NumberFormatException e) {
            log.warn("解析边界框坐标失败", e);
        }
    }

    private String getElementText(Document doc, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return StrUtil.EMPTY;
    }

    private String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return StrUtil.EMPTY;
    }
} 