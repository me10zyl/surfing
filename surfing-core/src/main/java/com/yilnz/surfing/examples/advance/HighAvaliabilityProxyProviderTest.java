package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.SurfSprider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.proxy.HighAvailabilityProxyProvider;
import com.yilnz.surfing.core.proxy.HttpProxy;

import java.util.Arrays;

public class HighAvaliabilityProxyProviderTest {
    public static void main(String[] args) {
        HttpProxy[] proxies = new HttpProxy[]{new HttpProxy("http://127.0.0.1:7777"), new HttpProxy("http://192.168.4.99:7777")};
        final SurfHttpRequest request = new SurfHttpRequest();
        request.setUrl("https://www.baidu.com");
        request.setMethod("GET");
        final SurfHttpRequest request2 = new SurfHttpRequest();
        request2.setUrl("https://www.tieba.com");
        request2.setMethod("GET");
        SurfSprider.create().setRequests(Arrays.asList(request, request2)).setProxyProvider(new HighAvailabilityProxyProvider(Arrays.asList(proxies))).setProcessor(new SurfPageProcessor() {
            @Override
            public void process(Page page) {
                System.out.println(page.getHtml());
            }
        }).start();
    }
}
