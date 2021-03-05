package com.yilnz.surfing.test.seletorsTest;

import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Html;
import org.junit.Test;

public class XPathSelectorTest {

    @Test
    public void test1(){
        Html html = SurfSpider.get("https://httpbin.org/forms/post").getHtml();
        System.out.println(html);
        html.selectXPath("//*[text()=' Bacon ']");
    }
}
