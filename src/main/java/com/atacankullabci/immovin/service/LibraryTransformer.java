package com.atacankullabci.immovin.service;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

public class LibraryTransformer {

    public String transform(byte[] fileContent) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();

        ClassLoader classLoader = getClass().getClassLoader();

        Source xslt = new StreamSource(classLoader.getResourceAsStream("transform.xslt"));
        Transformer transformer = factory.newTransformer(xslt);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileContent);
        Source text = new StreamSource(byteArrayInputStream);

        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        transformer.transform(text, result);
        return stringWriter.toString();
    }
}
