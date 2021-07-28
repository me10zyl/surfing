package com.yilnz.surfing.test.baseTest;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Header;
import com.yilnz.surfing.core.basic.Page;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class HeadersTest {
    @Test
    public void test1(){
        SurfHttpRequestBuilder builder = new SurfHttpRequestBuilder("https://httpbin.org/delete", "DELETE");
        SurfHttpRequest req = builder.build();
        Page page = SurfSpider.create().addRequest(req).request().get(0);
        List<Header> headers = page.getHeaders();
        for (Header header : headers) {
            System.out.println(header.getName() + ":" + header.getValue());
        }
        Assert.assertEquals(page.getHeader("Content-Type"), "application/json");
    }
}
