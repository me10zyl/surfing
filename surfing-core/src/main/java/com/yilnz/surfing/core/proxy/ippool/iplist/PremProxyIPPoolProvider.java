package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.header.generators.BulkHeaderGenerator;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;
import com.yilnz.surfing.core.proxy.ippool.IPPool;
import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;

import java.util.List;
import java.util.Random;

public class PremProxyIPPoolProvider implements IPPoolProvider {
    @Override
    public List<HttpProxy> getProxyList() {
            final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
            surfHttpRequest.setMethod("GET");
            surfHttpRequest.setConnectTimeout(5000);
            surfHttpRequest.setUrl("https://premproxy.com/proxy-by-country/Japan-01.htm");
            surfHttpRequest.setHeaderGenerator(new BulkHeaderGenerator("Host: premproxy.com\n" +
                    "Connection: keep-alive\n" +
                    "Cache-Control: max-age=0\n" +
                    "Upgrade-Insecure-Requests: 1\n" +
                    "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36\n" +
                    "Sec-Fetch-User: ?1\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\n" +
                    "Sec-Fetch-Site: none\n" +
                    "Sec-Fetch-Mode: navigate\n" +
                    "Accept-Encoding: gzip, deflate, br\n" +
                    "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\n" +
                    "Cookie: _ga=GA1.2.837956213.1575449980; _gid=GA1.2.814689320.1575449980; _gat=1"));
            return IPPool.extractProxyListFromURL(surfHttpRequest, new ProxyProvider() {
                @Override
                public HttpProxy getProxy() {
                    return new HttpProxy("127.0.0.1", 7777);
                }
            }, "#proxylist", 0, -1);
    }

    public static void main(String[] args) {
        new PremProxyIPPoolProvider().test();
    }
}
