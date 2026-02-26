package com.yilnz.surfing.test.baseTest;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import org.apache.http.conn.ConnectTimeoutException;
import org.junit.Assert;
import org.junit.Test;

import java.net.SocketTimeoutException;

/**
 * Base request Test
 */
public class GetPostTest {

    @Test
    public void testGet(){
        Page page = SurfSpider.get("http://www.baidu.com");
        Assert.assertEquals(page.getStatusCode(), 200);
        Assert.assertNotNull(page.getHtml().get());
    }

    @Test
    public void testDelay(){
        Page page = SurfSpider.post("https://httpbin.org/delay/45", null);
        Assert.assertEquals(page.getStatusCode(), 0);
    }

    @Test
    public void testDelay2(){
        SurfSpider surfSpider = SurfSpider.create();
        SurfHttpRequest request = new SurfHttpRequest("https://httpbin.org/delay/45");
        request.setConnectTimeout(50000);
        request.setMethod("POST");
        surfSpider.addRequest(request);
        Page page = surfSpider.request().get(0);
        Assert.assertEquals(page.getStatusCode(), 200);
    }


    @Test
    public void testPost(){
        Page page = SurfSpider.post("https://httpbin.org/post", null);
        System.out.println(page.getHtml());
        Assert.assertEquals(page.getStatusCode(), 200);
        Assert.assertNotNull(page.getHtml().get());
    }

    @Test
    public void testPut(){
        SurfHttpRequestBuilder builder = new SurfHttpRequestBuilder("https://httpbin.org/put", "PUT");
        SurfHttpRequest req = builder.build();
        Page page = SurfSpider.create().addRequest(req).request().get(0);
        System.out.println(page.getHtml());
        Assert.assertEquals(page.getStatusCode(), 200);
        Assert.assertNotNull(page.getHtml().get());
    }

    @Test
    public void testDelete(){
        SurfHttpRequestBuilder builder = new SurfHttpRequestBuilder("https://httpbin.org/delete", "DELETE");
        SurfHttpRequest req = builder.build();
        Page page = SurfSpider.create().addRequest(req).request().get(0);
        System.out.println(page.getHtml());
        Assert.assertEquals(page.getStatusCode(), 200);
        Assert.assertNotNull(page.getHtml().get());
    }
}
