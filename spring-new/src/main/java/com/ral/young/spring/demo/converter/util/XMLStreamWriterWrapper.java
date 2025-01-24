package com.ral.young.spring.demo.converter.util;

import lombok.Getter;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;

/**
 * XMLStreamWriter的AutoCloseable包装类
 */
@Getter
public class XMLStreamWriterWrapper implements AutoCloseable {
    private final XMLStreamWriter writer;

    public XMLStreamWriterWrapper(XMLStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void close() throws XMLStreamException {
        if (writer != null) {
            writer.close();
        }
    }
} 