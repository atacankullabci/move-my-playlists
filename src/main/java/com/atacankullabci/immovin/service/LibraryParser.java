package com.atacankullabci.immovin.service;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;

public class LibraryParser {

    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;
    private XPathFactory xpathFactory;
    private XPath xPath;

    public LibraryParser() {
        this.factory = DocumentBuilderFactory.newInstance();
        this.xpathFactory = XPathFactory.newInstance();
        setDocumentBuilder();
    }

    private void setDocumentBuilder() {
        if (this.factory != null) {
            try {
                this.builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDocument(File file) {
        if (this.builder != null) {
            try {
                document = this.builder.parse(file);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setXPath() {
        if (this.xpathFactory != null) {
            this.xPath = xpathFactory.newXPath();
        }
    }

    public void parser() {

        String name = getTrackNameById(this.document, this.xPath, 123);
        System.out.println("Track Name with ID " + 213 + ": " + name);
    }

    public String getTrackNameById(Document doc, XPath xpath, int id) {
        String name = null;
        try {
            XPathExpression expr = xpath.compile("/plist/dict/dict[key=\'5137\']/dict/key[. = 'Name']/following-sibling::*[1]/text()");
            name = (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return name;
    }
}
