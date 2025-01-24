package com.ral.young.spring.demo.converter.util;

import lombok.Getter;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

/**
 * XMLStreamReader的AutoCloseable包装类
 */
@Getter
public class XMLStreamReaderWrapper implements AutoCloseable {
    private final XMLStreamReader reader;

    public XMLStreamReaderWrapper(XMLStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void close() throws XMLStreamException {
        if (reader != null) {
            reader.close();
        }
    }
} 