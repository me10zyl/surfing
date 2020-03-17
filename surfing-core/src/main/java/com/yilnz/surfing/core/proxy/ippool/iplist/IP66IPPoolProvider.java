package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ippool.IPPool;
import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;
import com.yilnz.surfing.core.selectors.Selectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IP66IPPoolProvider implements IPPoolProvider {
    private final int pageCount = 10;
    @Override
    public List<HttpProxy> getProxyList() {
        final SurfHttpRequest request = new SurfHttpRequest("http://www.66ip.cn/index.html");
        request.setConnectTimeout(3000);
        final Html html = SurfSpider.get(request).getHtml();
        final Integer pageSize = html.select(Selectors.$("#PageList a:nth-last-of-type(2)")).getInt();
        final Random random = new Random();
        int ranPage = random.nextInt(pageSize - pageCount) + 1;
        final ArrayList<HttpProxy> all = new ArrayList<>();
        for( int i = ranPage;i <= ranPage + pageCount;i++) {
            List<HttpProxy> httpProxies = IPPool.extractProxyListFromURL("http://www.66ip.cn/" + ranPage + ".html", "#main");
            all.addAll(httpProxies);
        }
        return all;
    }

    public static void main(String[] args) {
        final List<HttpProxy> proxyList = new IP66IPPoolProvider().getProxyList();
        System.out.println(proxyList.size());
        System.out.println(proxyList);
    }
}
