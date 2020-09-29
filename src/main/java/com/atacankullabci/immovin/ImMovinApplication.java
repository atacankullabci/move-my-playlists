package com.atacankullabci.immovin;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class ImMovinApplication {

    public static void main(String[] args) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new File("lib-small.xml"));

            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();

            // Create XPath object
            XPath xpath = xpathFactory.newXPath();

            int id = 5137;
            //System.out.println(getTrackNameById(doc, xpath, 12));
            getAllKeys(doc, xpath);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        //SpringApplication.run(ImMovinApplication.class, args);
    }

    public static void getAllKeys(Document document, XPath xPath) {
        String response = "";
        try {
            XPathExpression expr = xPath.compile("//plist/dict/dict");
            response = (String) expr.evaluate(document, XPathConstants.STRING);
            System.out.println(response);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    public static String getTrackNameById(Document doc, XPath xpath, int id) {
        String name = null;
        try {
            XPathExpression expr = xpath.compile("/plist/dict/dict[key=\'5143\']/dict/key[. = 'Name']/following-sibling::*[1]/text()");
            name = (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return name;
    }


}
