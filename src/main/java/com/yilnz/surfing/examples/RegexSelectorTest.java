package com.yilnz.surfing.examples;

import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.SurfHttpClient;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;

import java.util.List;

public class RegexSelectorTest {
    public static void main(String[] args) {
        SurfHttpRequest request = new SurfHttpRequest();
        request.setUrl("http://www.baidu.com");
        final Page page = new SurfHttpClient().get(request);
        System.out.println(page.getHtml().get());
        System.out.println(page.getStatusCode());
        final Selectable select = page.getHtml().select(Selectors.regex("<a.+?>.+?</a>"));
        final List<Selectable> nodes = select.nodes();
        nodes.forEach(e->{
            System.out.println(e);
            System.out.println(e.select(Selectors.$("a", "name")));
        });
    }
}
