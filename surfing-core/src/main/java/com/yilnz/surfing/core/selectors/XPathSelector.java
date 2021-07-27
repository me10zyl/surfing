package com.yilnz.surfing.core.selectors;
import java.io.PrintWriter;
import java.util.Properties;
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
            NodeList nodeList = (NodeList) compile.evaluate(xmlDocument, XPathConstants.NODESET);
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
                results.add(sb.toString());
            }
        } catch (ParserConfigurationException | XPathExpressionException | IOException | SAXException e) {
            logger.error("[surfing]xpath select error", e);
        }
        return results;
    }

    private String htmlToXml(String text) {
        Tidy tidy = new Tidy();
        tidy.setShowWarnings(false);
        tidy.setWriteback(false);
        tidy.setOnlyErrors(false);
        tidy.setQuiet(false);
        tidy.setIndentContent(false);
        tidy.setSmartIndent(false);
        tidy.setHideEndTags(false);
        tidy.setXmlTags(false);
        tidy.setXmlOut(true);
        tidy.setXHTML(false);
        tidy.setUpperCaseTags(false);
        tidy.setUpperCaseAttrs(false);
        tidy.setMakeClean(false);
        tidy.setMakeBare(false);
        tidy.setBreakBeforeBR(false);
        tidy.setBurstSlides(false);
        tidy.setNumEntities(false);
        tidy.setQuoteMarks(false);
        tidy.setQuoteNbsp(false);
        tidy.setQuoteAmpersand(false);
        tidy.setWrapAttVals(false);
        tidy.setWrapScriptlets(false);
        tidy.setWrapSection(false);
        tidy.setXmlPi(false);
        tidy.setDropFontTags(false);
        tidy.setDropProprietaryAttributes(false);
        tidy.setDropEmptyParas(false);
        tidy.setFixComments(false);
        tidy.setWrapAsp(false);
        tidy.setWrapJste(false);
        tidy.setWrapPhp(false);
        tidy.setFixBackslash(false);
        tidy.setIndentAttributes(false);
        tidy.setLogicalEmphasis(false);
        tidy.setXmlPIs(false);
        tidy.setEncloseText(false);
        tidy.setEncloseBlockText(false);
        tidy.setWord2000(false);
        tidy.setTidyMark(false);
        tidy.setXmlSpace(false);
        tidy.setEmacs(false);
        tidy.setLiteralAttribs(false);
        tidy.setPrintBodyOnly(false);
        tidy.setFixUri(false);
        tidy.setLowerLiterals(false);
        tidy.setHideComments(false);
        tidy.setIndentCdata(false);
        tidy.setForceOutput(false);
        tidy.setAsciiChars(false);
        tidy.setJoinClasses(false);
        tidy.setJoinStyles(false);
        tidy.setTrimEmptyElements(false);
        tidy.setReplaceColor(false);
        tidy.setEscapeCdata(false);
        tidy.setKeepFileTimes(false);
        tidy.setRawOut(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tidy.parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), baos);
        //System.out.println("parsed:" + baos.toString());
        return baos.toString();
    }
}
