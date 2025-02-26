package com.ral.young.spring.demo;

import cn.hutool.core.util.StrUtil;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author renyunhui
 * @description XML和JsonPath解析器
 * @date 2024-01-20
 */
@Slf4j
public class XmlJsonPathParser {

    /**
     * 批量更新XML中Params属性的JSON数据
     * @param xmlContent XML内容
     * @param updates JsonPath和新值的映射关系
     * @return 更新后的XML字符串
     */
    public static String batchUpdateValuesInXmlParams(String xmlContent, Map<String, Object> updates) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

            NodeList allNodes = doc.getElementsByTagName("*");
            boolean hasUpdates = false;

            for (int i = 0; i < allNodes.getLength(); i++) {
                Element element = (Element) allNodes.item(i);
                if (element.hasAttribute("Params")) {
                    String paramsJson = element.getAttribute("Params");
                    if (StrUtil.isNotBlank(paramsJson)) {
                        boolean nodeUpdated = false;
                        DocumentContext jsonContext = JsonPath.parse(paramsJson);

                        for (Map.Entry<String, Object> update : updates.entrySet()) {
                            try {
                                // 尝试更新值，如果路径不存在会抛出异常
                                jsonContext.set(update.getKey(), update.getValue());
                                nodeUpdated = true;
                            } catch (Exception e) {
                                // 路径不存在或其他错误，继续下一个
                                log.debug("节点 {} 不包含路径 {}", element.getNodeName(), update.getKey());
                            }
                        }

                        if (nodeUpdated) {
                            // 直接使用更新后的JSON字符串
                            element.setAttribute("Params", jsonContext.jsonString());
                            hasUpdates = true;
                        }
                    }
                }
            }

            return hasUpdates ? formatXml(doc) : xmlContent;
        } catch (Exception e) {
            log.error("更新XML失败", e);
            return xmlContent;
        }
    }

    /**
     * 格式化XML文档
     */
    private static String formatXml(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");  // 修改这里，设置为 "yes" 来移除 XML 声明

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        // 使用StringBuilder进行字符串处理
        StringBuilder result = new StringBuilder(writer.toString());
        NodeList allNodes = doc.getElementsByTagName("*");

        // 从后向前处理节点，避免替换位置影响
        for (int i = allNodes.getLength() - 1; i >= 0; i--) {
            Element element = (Element) allNodes.item(i);
            if (element.hasAttribute("Params")) {
                String nodeName = element.getNodeName();
                String params = element.getAttribute("Params");

                int startPos = result.indexOf("<" + nodeName);
                int endPos = result.indexOf("/>", startPos) + 2;
                if (startPos >= 0 && endPos > startPos) {
                    String replacement = "<" + nodeName + " DispatcherType=\"Sync\" Params='" + params + "'/>";
                    result.replace(startPos, endPos, replacement);
                }
            }
        }

        return result.toString().trim();  // 添加 trim() 移除可能的前后空白
    }

    // 测试方法
    public static void main(String[] args) {
        try {
            // 输入输出文件路径
            String inputFilePath = "C:\\Users\\Administrator\\Desktop\\学习中心v1.5\\测试能力模板转换功能\\原始能力模板.xml";
            String outputFilePath = "C:\\Users\\Administrator\\Desktop\\学习中心v1.5\\测试能力模板转换功能\\转换后的能力模板.xml";

            // 读取XML文件
            String xmlContent = readFile(inputFilePath);
            if (xmlContent == null) {
                log.info("读取文件失败！");
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("$.models", new String[]{"NewModel1", "NewModel2"});
            updates.put("$.labels", new String[]{"plastic_bag", "bottle", "bottle_pile"});

            String updatedXml = batchUpdateValuesInXmlParams(xmlContent, updates);

            // 将更新后的内容写入新文件
            if (writeFile(updatedXml, outputFilePath)) {
                log.info("文件已成功写入到: {}", outputFilePath);
            } else {
                log.info("文件写入失败！");
            }

        } catch (Exception e) {
            log.error("处理过程中发生错误", e);
        }
    }

    /**
     * 从文件读取内容
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("读取文件失败", e);
            return null;
        }
    }

    /**
     * 写入内容到文件
     * @param content 要写入的内容
     * @param filePath 文件路径
     * @return 是否写入成功
     */
    public static boolean writeFile(String content, String filePath) {
        try {
            Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            log.error("写入文件失败", e);
            return false;
        }
    }
}