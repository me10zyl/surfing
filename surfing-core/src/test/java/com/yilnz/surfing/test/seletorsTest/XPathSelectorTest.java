package com.yilnz.surfing.test.seletorsTest;

import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.HtmlNode;
import org.ccil.cowan.tagsoup.Parser;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.ant.JTidyTask;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import us.codecraft.xsoup.Xsoup;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class XPathSelectorTest {

    @Test
    public void test1(){
        Html html = SurfSpider.get("https://httpbin.org/forms/post").getHtml();
        System.out.println(html);
        HtmlNode htmlNode = html.selectXPath("//*[text()='Bacon']");
        Assert.assertEquals(htmlNode.attr("type"), "checkbox");
    }

    @Test
    public void testXSoup() throws XPathExpressionException, ParserConfigurationException, IOException, SAXException, TransformerException {
        //Xsoup.compile("//*[text()=' Bacon ']");
        /*Html html = SurfSpider.get("https://httpbin.org/forms/post").getHtml();
        System.out.println(html);
        Parser parser = new Parser();
        ByteArrayInputStream source = new ByteArrayInputStream(html.toString().getBytes(StandardCharsets.UTF_8));
       *//* DOMResult result = new DOMResult();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new SAXSource(parser, new InputSource(source)), result);
        Document xmlDocument = (Document) result.getNode();
        System.out.println(xmlDocument);*//*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //tidy.parse(new ByteArrayInputStream(html.toString().getBytes(StandardCharsets.UTF_8)), baos);
        String s =  baos.toString();
        System.out.println(s);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument1 = builder.parse(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression compile = xPath.compile("//*[text()=' Bacon ']");
        XPathExpression compile2 = xPath.compile("//*[@name='topping']");
        String evaluate = compile.evaluate(xmlDocument);
        NodeList nodeList = (NodeList) compile2.evaluate(xmlDocument, XPathConstants.NODESET);
        System.out.println(" ?? " + evaluate);
        for(int i = 0;i < nodeList.getLength();i++){
            Node item = nodeList.item(i);
            String nodeName = item.getNodeName();
            NamedNodeMap attributes = item.getAttributes();
            String textContent = item.getTextContent();
            StringBuilder sb = new StringBuilder("<");
            sb.append(nodeName).append(" ");
            for(int j = 0;j < attributes.getLength();j++){
                Node node = attributes.item(j);
                String nodeValue = node.getNodeValue();
                String nodeName1 = node.getNodeName();
                sb.append(nodeName1).append("=").append("\"").append(nodeValue).append("\"").append(" ");
            }
            if(attributes.getLength() == 0){
                sb.append(" ");
            }
            sb.append(">").append(textContent).append("</" + nodeName + ">");
            System.out.println(sb);
        }*/

    }
}
