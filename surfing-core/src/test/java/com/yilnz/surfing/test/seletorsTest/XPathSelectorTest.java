package com.yilnz.surfing.test.seletorsTest;

import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.HtmlNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPather;
import org.htmlcleaner.XPatherException;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
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
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class XPathSelectorTest {

    @Test
    public void test1(){
        Html html = SurfSpider.get("https://httpbin.org/forms/post").getHtml();
        System.out.println(html);
        HtmlNode htmlNode = html.selectXPath("//*[text()=' Bacon ']");
        Assert.assertEquals(htmlNode.get().trim(), "Bacon");
        System.out.println(htmlNode.outerHtml());
        Assert.assertEquals(htmlNode.selectXPath("//input").selectCss("input", "value").get(), "bacon");
    }

    @Test
    public void testHtmlCleaner() throws XPatherException, ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {
        Html html = SurfSpider.get("https://httpbin.org/forms/post").getHtml();
        //System.out.println(html);
        HtmlCleaner cleaner = new HtmlCleaner();
        XPather xPather = new XPather("//*[text()=' Bacon ']");
        TagNode clean = cleaner.clean(html.get());
        SimpleHtmlSerializer x = new SimpleHtmlSerializer(cleaner.getProperties());
        String s = x.getAsString(clean);
        System.out.println(s);
        Object[] myNodes = xPather.evaluateAgainstNode(clean);
        for (Object myNode : myNodes) {
            System.out.println(myNode);
        }

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression compile = xPath.compile("//*[text()=' Bacon ']");
        NodeList nodesList = (NodeList) compile.evaluate(xmlDocument, XPathConstants.NODESET);
        for(int i = 0;i < nodesList.getLength();i++){
            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(nodesList.item(i)), new StreamResult(writer));
            String xml = writer.toString();
            //xml = xml.substring(xml.indexOf("?>") + 2);
            System.out.println("evaluate result:" + xml);
        }

    }

}
