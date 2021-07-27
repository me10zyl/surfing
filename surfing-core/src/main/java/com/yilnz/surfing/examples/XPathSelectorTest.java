package com.yilnz.surfing.examples;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;

import java.util.List;

public class XPathSelectorTest {
    public static void main(String[] args) {
        SurfHttpRequest request = new SurfHttpRequest();
        request.setUrl("https://www.baidu.com");
        request.setMethod("Get");
        final Page page = SurfSpider.create().addRequest(request).request().get(0);
        final Selectable select = page.getHtml().select(Selectors.xpath("//a/text()"));
        final List<? extends Selectable> nodes = select.nodes();
        nodes.forEach(e->{
            System.out.println(e);
        });
    }
}
