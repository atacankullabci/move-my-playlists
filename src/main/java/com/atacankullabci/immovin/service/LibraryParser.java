package com.atacankullabci.immovin.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class LibraryParser {

    public static final String tracksXpath = "//dict/key[. = 'Name']/following-sibling::*[1]/text()";
    public static final String artistsXpath = "//dict/key[. = 'Artist']/following-sibling::*[1]/text()";
    public static final String albumsXpath = "//dict/key[. = 'Album']/following-sibling::*[1]/text()";

    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;
    private XPathFactory xpathFactory;
    private XPath xPath;

    public void createLibraryParser(File filePath) {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        document = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(filePath.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            document = builder.parse(new ByteArrayInputStream(fileContent));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        xpathFactory = XPathFactory.newInstance();
        xPath = xpathFactory.newXPath();
    }

    public NodeList getNodeList(String expression) {
        NodeList nodeList = null;
        try {
            XPathExpression xPathExpression = xPath.compile(expression);
            nodeList = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return nodeList;
    }
}
