package com.ral.young.spring.demo;

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

/**
 * @author renyunhui
 * @description XML和JsonPath解析器
 * @date 2024-01-20
 */
@Slf4j
public class XmlJsonPathParser {

    /**
     * 从XML中查找指定节点的Params属性中的JSON数据
     * @param xmlContent XML内容
     * @param nodeName 要查找的节点名称
     * @param jsonPath 要查找的JsonPath表达式
     * @return 查找到的值
     */
    public static Object findValueInXmlNodeParams(String xmlContent, String nodeName, String jsonPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

            // 查找指定的节点
            NodeList nodeList = doc.getElementsByTagName(nodeName);
            if (nodeList.getLength() > 0) {
                Element element = (Element) nodeList.item(0);
                String paramsJson = element.getAttribute("Params");

                if (!paramsJson.isEmpty()) {
                    // 使用JsonPath解析JSON字符串
                    return JsonPath.read(paramsJson, jsonPath);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("解析XML失败", e);
            return null;
        }
    }

    /**
     * 更新XML节点中Params属性的JSON数据
     * @param xmlContent XML内容
     * @param nodeName 要更新的节点名称
     * @param jsonPath 要更新的JsonPath表达式
     * @param newValue 新的值
     * @return 更新后的XML字符串
     */
    public static String updateValueInXmlNodeParams(String xmlContent, String nodeName, String jsonPath, Object newValue) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

            NodeList nodeList = doc.getElementsByTagName(nodeName);
            if (nodeList.getLength() > 0) {
                Element element = (Element) nodeList.item(0);
                String paramsJson = element.getAttribute("Params");

                if (!paramsJson.isEmpty()) {
                    // 使用JsonPath更新JSON字符串
                    DocumentContext jsonContext = JsonPath.parse(paramsJson);
                    jsonContext.set(jsonPath, newValue);
                    String updatedJson = jsonContext.jsonString();

                    // 直接更新节点的Params属性，不进行转义
                    element.setAttribute("Params", updatedJson);

                    // 使用自定义方法将DOM转换为字符串
                    return domToString(doc);
                }
            }
            return xmlContent;
        } catch (Exception e) {
            log.error("更新XML失败", e);
            return xmlContent;
        }
    }

    private static String domToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        // 设置输出属性
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        // 使用StringBuilder来构建输出
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        // 手动替换转义字符
        return writer.toString()
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
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

            // 首先查看当前值
            Object currentModels = findValueInXmlNodeParams(xmlContent, "KLDetection", "$.models");
            log.info("当前models值: {}", currentModels);

            // 更新KLDetection节点中的models数组
            String updatedXml = updateValueInXmlNodeParams(
                    xmlContent,
                    "KLDetection",
                    "$.models",
                    new String[]{"NewModel1", "NewModel2"}
            );

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
} 