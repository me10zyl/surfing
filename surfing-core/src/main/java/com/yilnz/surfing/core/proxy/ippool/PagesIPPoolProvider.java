package com.yilnz.surfing.core.proxy.ippool;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.header.generators.BulkHeaderGenerator;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public  abstract class PagesIPPoolProvider implements IPPoolProvider {

    private int willBeCrawledPage;
    private int assumPageCount;
    private String tableCssSelector;

    public PagesIPPoolProvider(int willBeCrawledPage, int assumPageCount, String tableCssSelector) {
        this.willBeCrawledPage = willBeCrawledPage;
        this.assumPageCount = assumPageCount;
        this.tableCssSelector = tableCssSelector;
    }

    public abstract String getPagedURL(int page);

    public ProxyProvider getProxyProvider(){
        return null;
    }

    public SurfHttpRequest getCustomRequest(){
        final SurfHttpRequest request = new SurfHttpRequest();
        request.setMethod("GET");
        request.setConnectTimeout(6000);
        request.setHeaderGenerator(new BulkHeaderGenerator("Connection: keep-alive\n" +
                "Cache-Control: max-age=0\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36\n" +
                "Sec-Fetch-User: ?1\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\n" +
                "Sec-Fetch-Site: none\n" +
                "Sec-Fetch-Mode: navigate\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\n" +
                "Cookie: _free_proxy_session=BAh7B0kiD3Nlc3Npb25faWQGOgZFVEkiJTNkNjg3NjA0Y2Y3ZmRlM2RlYzQyMGI0M2I5ZTQ2N2E5BjsAVEkiEF9jc3JmX3Rva2VuBjsARkkiMStiWFVMcERRbDN4UDZqV1RTZ0dmQjBuMU5ROU5MT1FqWEFvWXNrZSt2Wk09BjsARg%3D%3D--b2b99afbab5a6ac508e7b1f2dc8c6f11071aa631; Hm_lvt_0cf76c77469e965d2957f0553e6ecf59=1575275614,1575275734,1575275738,1575275739; Hm_lpvt_0cf76c77469e965d2957f0553e6ecf59=1575344636\n" +
                "If-None-Match: W/\"d1ccb5e3391d5b02cc58a5e389426f27\""));
        return request;
    }

    @Override
    public List<HttpProxy> getProxyList() {
        final Random random = new Random();
        int ranPage = 0;
        if (willBeCrawledPage != -1) {
            ranPage = random.nextInt(assumPageCount - willBeCrawledPage) + 1;
        }
        final ArrayList<HttpProxy> all = new ArrayList<>();
        if (willBeCrawledPage == -1) {
            extract(all, 0);
        }else {
            for (int i = ranPage; i <= ranPage + willBeCrawledPage; i++) {
                extract(all, i);
            }
        }
        return all;
    }

    public int[] getProxyFragmentIndex(){
        return new int[]{0, 1, -1};
    }

    protected void extract(ArrayList<HttpProxy> all, int i) {
        final String pagedURL = getPagedURL(i);
        final SurfHttpRequest customRequest = getCustomRequest();
        customRequest.setUrl(pagedURL);
        List<HttpProxy> httpProxies =  IPPool.extractProxyListFromURL(customRequest, getProxyProvider(), this.tableCssSelector, getProxyFragmentIndex()[0], getProxyFragmentIndex()[1],getProxyFragmentIndex()[2], null);
        all.addAll(httpProxies);
    }
}
