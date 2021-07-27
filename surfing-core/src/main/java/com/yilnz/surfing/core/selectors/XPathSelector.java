package com.yilnz.surfing.core.selectors;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import java.util.List;

public class XPathSelector extends Selector {

    private Logger logger = LoggerFactory.getLogger(XPathSelector.class);
    XPathEvaluator evaluator;
    public XPathSelector(String selectPattern) {
        super(selectPattern);
        evaluator = Xsoup.compile(selectPattern);
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

        final XElements evaluate = evaluator.evaluate(Jsoup.parse(text));
        return evaluate.list();
    }
}
