package com.yilnz.surfing.test.syncAndAsyncTest;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SyncAsyncTest {

    @Test
    public void testSync(){
        SurfHttpRequestBuilder builder = new SurfHttpRequestBuilder("http://www.httpbin.org/get", "GET");
        SurfHttpRequest r = builder.build();
        Page page = SurfSpider.create().addRequest(r).request().get(0);
        Assert.assertEquals(200, page.getStatusCode());
    }

    @Test
    public void testAsync() throws ExecutionException, InterruptedException {
        SurfHttpRequestBuilder builder = new SurfHttpRequestBuilder("http://www.httpbin.org/get", "GET");
        SurfHttpRequest r = builder.build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        List<Future<Page>> futures = SurfSpider.create().addRequest(r).setProcessor(new SurfPageProcessor() {
            @Override
            public void process(Page page) {
                countDownLatch.countDown();
                Assert.assertEquals(200, page.getStatusCode());
            }
        }).start();
        countDownLatch.await();
        for (Future<Page> future : futures) {
            Page page = future.get();
            Assert.assertEquals(200, page.getStatusCode());
        }
    }
}
