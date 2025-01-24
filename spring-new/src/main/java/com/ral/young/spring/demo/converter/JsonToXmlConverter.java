package com.ral.young.spring.demo.converter;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ral.young.spring.demo.converter.util.ConversionUtils;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.ral.young.spring.demo.converter.ConversionMetrics.recordMetrics;

@Slf4j
public class JsonToXmlConverter implements FormatConverter {

    @Override
    public String convert(String input, ConvertConfig config) {
        try {
            long startTime = System.currentTimeMillis();
            String result = doConvert(input);
            recordMetrics("json2xml", true, System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            recordMetrics("json2xml", false, 0);
            log.error("JSON转XML失败", e);
            throw new ConversionException("JSON转XML失败", e);
        }
    }

    private String doConvert(String input) {
        JSONObject json = JSONUtil.parseObj(input);
        Document doc = XmlUtil.createXml();
        Element root = doc.createElement("annotation");
        doc.appendChild(root);

        // 处理文件信息
        JSONObject fileInfo = json.getJSONObject("file");
        createElement(doc, root, "folder", "images");
        createElement(doc, root, "filename", fileInfo.getStr("name"));

        // 处理source信息
        Element source = createElement(doc, root, "source", null);
        createElement(doc, source, "database", "Unknown");

        // 处理size信息
        Element size = createElement(doc, root, "size", null);
        createElement(doc, size, "width", fileInfo.getStr("width"));
        createElement(doc, size, "height", fileInfo.getStr("height"));
        createElement(doc, size, "depth", ConversionUtils.DEFAULT_DEPTH);

        createElement(doc, root, "segmented", ConversionUtils.DEFAULT_SEGMENTED);

        // 处理标注信息
        JSONArray annotations = json.getJSONArray("annotation");
        for (int i = 0; i < annotations.size(); i++) {
            processAnnotation(doc, root, annotations.getJSONObject(i));
        }

        return XmlUtil.toStr(doc);
    }

    private void processAnnotation(Document doc, Element root, JSONObject anno) {
        Element object = createElement(doc, root, "object", null);
        
        createElement(doc, object, "name", anno.getStr("labelName"));
        createElement(doc, object, "pose", ConversionUtils.DEFAULT_POSE);
        createElement(doc, object, "truncated", ConversionUtils.DEFAULT_TRUNCATED);
        createElement(doc, object, "difficult", ConversionUtils.DEFAULT_DIFFICULT);

        // 处理边界框
        JSONArray points = anno.getJSONArray("points");
        if (points != null && points.size() >= 4) {
            int[] coords = ConversionUtils.getBoundingBoxCoordinates(points);
            if (coords != null) {
                Element bndbox = createElement(doc, object, "bndbox", null);
                createElement(doc, bndbox, "xmin", String.valueOf(coords[0]));
                createElement(doc, bndbox, "ymin", String.valueOf(coords[1]));
                createElement(doc, bndbox, "xmax", String.valueOf(coords[2]));
                createElement(doc, bndbox, "ymax", String.valueOf(coords[3]));
            }
        }
    }

    private Element createElement(Document doc, Element parent, String tagName, String content) {
        Element element = doc.createElement(tagName);
        if (content != null) {
            element.setTextContent(content);
        }
        parent.appendChild(element);
        return element;
    }
} 