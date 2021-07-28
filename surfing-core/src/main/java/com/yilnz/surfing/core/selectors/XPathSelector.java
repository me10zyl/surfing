package com.yilnz.surfing.core.selectors;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPather;
import org.w3c.tidy.TidyMessageListener;

import org.jsoup.Jsoup;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;
import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class XPathSelector extends Selector {

    private Logger logger = LoggerFactory.getLogger(XPathSelector.class);
    public XPathSelector(String selectPattern) {
        super(selectPattern);
    }

    @Override
    public List<String> selectList(String text) {
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        dbf.setValidating(false);
//        List<String> arr = new ArrayList<>();
//        try {
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            Document doc = db.parse(text);
//
//            XPathFactory factory = XPathFactory.newInstance();
//
//            XPath xpath = factory.newXPath();
//            final List<Node> nodes = (List<Node>)xpath.evaluate(selectPattern, doc, XPathConstants.NODESET);
//            nodes.forEach(e->{
//                arr.add(e.toString());
//            });
//        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
//           logger.error("[surfing]xpath select error", e);
//        }
        List<String> results = new ArrayList<>();
        try {
            String s =  htmlToXml(text);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression compile = xPath.compile(selectPattern);
            NodeList nodesList = (NodeList) compile.evaluate(xmlDocument, XPathConstants.NODESET);
            for(int i = 0;i < nodesList.getLength();i++){
                StringWriter writer = new StringWriter();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.transform(new DOMSource(nodesList.item(i)), new StreamResult(writer));
                String xml = writer.toString();
                results.add(xml);
            }
        } catch (ParserConfigurationException | XPathExpressionException | IOException | SAXException | TransformerException e) {
            logger.error("[surfing]xpath select error", e);
        }
        return results;
    }

    private String htmlToXml(String text) {
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode clean = cleaner.clean(text);
        SimpleHtmlSerializer x = new SimpleHtmlSerializer(cleaner.getProperties());
        String s = x.getAsString(clean);
        return s;
    }
}
