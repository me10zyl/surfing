package com.yilnz.surfing.examples;

import com.yilnz.surfing.core.SurfHttpClient;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selectors;

public class ReplaceSelectorTest {
    public static void main(String[] args) {
        SurfHttpRequest request = new SurfHttpRequest();
        request.setUrl("http://tieba.baidu.com/f?kw=java&fr=index");
        final Page page = new SurfHttpClient().get(request);
        final Html html = page.getHtml();
        System.out.println(html.select(Selectors.$("a.pagination-item:last-of-type", "href"))
                .select(Selectors.replace("&pn=.+", "")));
    }
}
