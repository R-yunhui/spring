package com.ral.young.spring.demo.converter;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ral.young.spring.demo.converter.util.ConversionUtils;
import com.ral.young.spring.demo.converter.util.XMLStreamWriterWrapper;
import com.ral.young.spring.demo.converter.util.XMLStreamReaderWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.xml.stream.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
public class LargeFileConverter {

    private static final int BUFFER_SIZE = 32768;
    private static final int BATCH_SIZE = 1000;
    private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();

    /**
     * 处理大文件
     */
    public void processLargeFile(File inputFile, File outputFile, boolean isJsonToXml) {
        long startTime = System.currentTimeMillis();
        long fileSize = inputFile.length();
        log.info("开始处理大文件: {}, 文件大小: {}MB",
                inputFile.getName(),
                String.format("%.2f", fileSize / (1024.0 * 1024.0)));

        try (BufferedInputStream input = new BufferedInputStream(Files.newInputStream(inputFile.toPath()), BUFFER_SIZE);
             BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(outputFile.toPath()), BUFFER_SIZE)) {

            if (isJsonToXml) {
                jsonToXmlStream(input, output);
            } else {
                xmlToJsonStream(input, output);
            }

            long endTime = System.currentTimeMillis();
            log.info("大文件处理完成: {}, 耗时: {}秒",
                    inputFile.getName(),
                    String.format("%.2f", (endTime - startTime) / 1000.0));

        } catch (Exception e) {
            log.error("处理大文件失败: {}", inputFile.getName(), e);
            try {
                Files.deleteIfExists(outputFile.toPath());
            } catch (IOException deleteError) {
                log.error("删除未完成的输出文件失败: {}", outputFile.getName(), deleteError);
            }
            throw new ConversionException("处理大文件失败", e);
        }
    }

    /**
     * JSON转XML流式处理
     */
    private void jsonToXmlStream(InputStream input, OutputStream output) throws Exception {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8), BUFFER_SIZE);
             XMLStreamWriterWrapper writerWrapper = new XMLStreamWriterWrapper(factory.createXMLStreamWriter(output, DEFAULT_ENCODING))) {

            XMLStreamWriter writer = writerWrapper.getWriter();
            // 读取整个JSON文件内容
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // 解析完整的JSON对象
            JSONObject json = JSONUtil.parseObj(jsonContent.toString());

            // 开始写XML文档
            writer.writeStartDocument(DEFAULT_ENCODING, "1.0");
            writer.writeStartElement("annotation");

            // 处理文件信息
            JSONObject fileInfo = json.getJSONObject("file");
            if (fileInfo != null) {
                writeElement(writer, "folder", "images");
                writeElement(writer, "filename", fileInfo.getStr("name"));

                writer.writeStartElement("source");
                writeElement(writer, "database", "Unknown");
                writer.writeEndElement();

                writer.writeStartElement("size");
                writeElement(writer, "width", fileInfo.getStr("width"));
                writeElement(writer, "height", fileInfo.getStr("height"));
                writeElement(writer, "depth", ConversionUtils.DEFAULT_DEPTH);
                writer.writeEndElement();

                writeElement(writer, "segmented", ConversionUtils.DEFAULT_SEGMENTED);
            }

            // 处理标注信息
            JSONArray annotations = json.getJSONArray("annotation");
            if (annotations != null) {
                int total = annotations.size();
                for (int i = 0; i < total; i++) {
                    JSONObject anno = annotations.getJSONObject(i);
                    writeAnnotation(writer, anno);
                }
            }

            // 结束XML文档
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
        }
    }

    /**
     * 写入单个标注信息
     */
    private void writeAnnotation(XMLStreamWriter writer, JSONObject anno) throws XMLStreamException {
        writer.writeStartElement("object");

        writeElement(writer, "name", anno.getStr("labelName"));
        writeElement(writer, "pose", ConversionUtils.DEFAULT_POSE);
        writeElement(writer, "truncated", ConversionUtils.DEFAULT_TRUNCATED);
        writeElement(writer, "difficult", ConversionUtils.DEFAULT_DIFFICULT);

        // 写入边界框
        JSONArray points = anno.getJSONArray("points");
        if (points != null && points.size() >= 4) {
            int[] coords = ConversionUtils.getBoundingBoxCoordinates(points);
            if (coords != null) {
                writer.writeStartElement("bndbox");
                writeElement(writer, "xmin", String.valueOf(coords[0]));
                writeElement(writer, "ymin", String.valueOf(coords[1]));
                writeElement(writer, "xmax", String.valueOf(coords[2]));
                writeElement(writer, "ymax", String.valueOf(coords[3]));
                writer.writeEndElement();
            }
        }

        writer.writeEndElement(); // 结束object
    }

    /**
     * XML转JSON流式处理
     */
    private void xmlToJsonStream(InputStream input, OutputStream output) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), BUFFER_SIZE);
             XMLStreamReaderWrapper readerWrapper = new XMLStreamReaderWrapper(factory.createXMLStreamReader(input, DEFAULT_ENCODING))) {

            XMLStreamReader reader = readerWrapper.getReader();
            JSONObject rootJson = new JSONObject();
            JSONObject fileInfo = new JSONObject();
            JSONArray annotations = new JSONArray();
            JSONObject currentObject = null;

            rootJson.set("file", fileInfo);
            rootJson.set("annotation", annotations);

            int elementCount = 0;
            String currentElement;

            while (reader.hasNext()) {
                int event = reader.next();
                elementCount++;

                if (event == XMLStreamConstants.START_ELEMENT) {
                    currentElement = reader.getLocalName();
                    switch (currentElement) {
                        case "filename":
                            fileInfo.set("name", reader.getElementText());
                            break;
                        case "width":
                            fileInfo.set("width", reader.getElementText());
                            break;
                        case "height":
                            fileInfo.set("height", reader.getElementText());
                            break;
                        case "object":
                            currentObject = new JSONObject();
                            annotations.add(currentObject);
                            break;
                        case "name":
                            if (currentObject != null) {
                                String labelName = reader.getElementText();
                                currentObject.set("labelName", labelName);
                                currentObject.set("labelCode", ConversionUtils.getLabelCode(labelName, null));
                            }
                            break;
                        case "bndbox":
                            if (currentObject != null) {
                                processXmlBoundingBox(reader, currentObject);
                            }
                            break;
                    }
                }
            }

            // 写入最终的JSON结果
            writer.write(rootJson.toStringPretty());
            writer.flush();
        }
    }

    /**
     * 处理XML边界框
     */
    private void processXmlBoundingBox(XMLStreamReader reader, JSONObject currentObject) throws XMLStreamException {
        String xmin = null, ymin = null, xmax = null, ymax = null;

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();
                switch (elementName) {
                    case "xmin":
                        xmin = reader.getElementText();
                        break;
                    case "ymin":
                        ymin = reader.getElementText();
                        break;
                    case "xmax":
                        xmax = reader.getElementText();
                        break;
                    case "ymax":
                        ymax = reader.getElementText();
                        break;
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && "bndbox".equals(reader.getLocalName())) {
                break;
            }
        }

        if (xmin != null && ymin != null && xmax != null && ymax != null) {
            JSONArray points = ConversionUtils.createBoundingBoxPoints(
                    Integer.parseInt(xmin),
                    Integer.parseInt(ymin),
                    Integer.parseInt(xmax),
                    Integer.parseInt(ymax)
            );
            currentObject.set("points", points);
        } else {
            log.warn("边界框数据不完整: xmin={}, ymin={}, xmax={}, ymax={}", xmin, ymin, xmax, ymax);
        }
    }

    /**
     * 写入XML元素
     */
    private void writeElement(XMLStreamWriter writer, String name, String value) throws XMLStreamException {
        writer.writeStartElement(name);
        writer.writeCharacters(value);
        writer.writeEndElement();
    }

    /**
     * 安全关闭资源
     */
    private void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                log.warn("关闭资源失败", e);
            }
        }
    }
} 