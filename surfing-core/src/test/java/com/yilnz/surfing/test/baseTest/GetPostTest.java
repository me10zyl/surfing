package com.yilnz.surfing.test.baseTest;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import org.junit.Assert;
import org.junit.Test;

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
