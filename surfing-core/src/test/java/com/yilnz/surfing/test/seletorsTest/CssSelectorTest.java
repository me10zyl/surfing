package com.yilnz.surfing.test.seletorsTest;

import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.HtmlNode;
import com.yilnz.surfing.core.selectors.Selectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

public class CssSelectorTest {

    @Test
    public void testCss1(){
        Html html = SurfSpider.get("https://httpbin.org/forms/post").getHtml();
        System.out.println(html);
        HtmlNode fieldset = html.selectCss("fieldset");
        Assert.assertEquals("Pizza Size", fieldset.selectCss("legend").get());
        Assert.assertEquals("Pizza Toppings", fieldset.nodes().get(1).selectCss("legend").get());
        HtmlNode htmlNode = html.selectCss("[type=checkbox]");
        Assert.assertEquals(htmlNode.nodes().size(), 4);
        Assert.assertEquals(html.select(Selectors.$("[type=checkbox]","value")).get(), "bacon");
        Assert.assertEquals(((HtmlNode)html.select(Selectors.$("[type=checkbox]"))).outerHtml(), "<input type=\"checkbox\" name=\"topping\" value=\"bacon\">");
        System.out.println(htmlNode.nodes().stream().map(HtmlNode::outerHtml).collect(Collectors.joining(",")));
        htmlNode.selectCss("[value]").nodes().forEach(n->{
            System.out.println("inputs:" + n.outerHtml());
        });
        html.selectCss("label").nodes().forEach(n->{
            System.out.println("label value: " +n);
        });
    }

    @Test
    public void testJsoup(){
        Html html = SurfSpider.get("https://httpbin.org/forms/post").getHtml();
        System.out.println(html);
        Document document = Jsoup.parse(html.get());
        Elements elements = document.select("[type=checkbox]");
        System.out.println(elements);
        String text = elements.get(0).attr("value");
        Assert.assertEquals("bacon", text);

        String html1 = elements.get(0).toString();
        System.out.println("html1:"+ html1);
        Document parse = Jsoup.parse(html1);
        System.out.println(parse.select("[type=checkbox]").attr("value"));

        String text1 = elements.select("[value=bacon]").get(0).text();
        Assert.assertEquals("bacon", text);

    }
}
