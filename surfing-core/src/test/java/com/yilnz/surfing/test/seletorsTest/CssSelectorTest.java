package com.yilnz.surfing.test.seletorsTest;

import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.HtmlNode;
import org.junit.Assert;
import org.junit.Test;

public class CssSelectorTest {

    @Test
    public void testCss1(){
        Html html = SurfSpider.get("https://httpbin.org/forms/post").getHtml();
        HtmlNode fieldset = html.selectCss("fieldset");
        Assert.assertEquals("Pizza Size", fieldset.selectCss("legend").get());
        Assert.assertEquals("Pizza Toppings", fieldset.nodes().get(1).selectCss("legend").get());
    }
}
