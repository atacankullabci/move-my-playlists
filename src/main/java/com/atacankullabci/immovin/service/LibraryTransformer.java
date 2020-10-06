package com.atacankullabci.immovin.service;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

public class LibraryTransformer {

    public static String transform(String fileContent) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File("transform.xslt"));
        Transformer transformer = factory.newTransformer(xslt);

        StringReader stringReader = new StringReader(fileContent);
        Source text = new StreamSource(stringReader);

        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        transformer.transform(text, result);
        return stringWriter.toString();
    }
}
