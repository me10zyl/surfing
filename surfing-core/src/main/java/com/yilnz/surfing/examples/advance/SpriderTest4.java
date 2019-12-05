package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.Site;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.SurfSprider;
import com.yilnz.surfing.core.basic.Page;

public class SpriderTest4 {
    public static void main(String[] args) {
        final SurfHttpRequest request = new SurfHttpRequest();
        request.setUrl("https://www.baidu.com");
        request.setMethod("GET");
        final SurfHttpRequest request2 = new SurfHttpRequest();
        request2.setUrl("https://www.baidu.com");
        request2.setMethod("GET");
        final SurfHttpRequest request3 = new SurfHttpRequest();
        request3.setUrl("http://www.baidu.com/s");
        request3.addParams("wd", "java");
        request3.setMethod("GET");
        SurfSprider.create().
                addRequest(request)
                .addRequest(request2)
                .addRequest(request3).thread(5).setProcessor(new SurfPageProcessor() {
            @Override
            public Site getSite() {
                return Site.me().setRetryTimes(2).setSleepTime(500);
            }

            @Override
            public void process(Page page) {
                System.out.println(page.getUrl());
            }

            @Override
            public void processError(Page page) {
                System.out.println(page.getUrl() +"-- error");
            }
        }).start();

    }
}
