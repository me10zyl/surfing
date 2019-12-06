package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.header.generators.BulkHeaderGenerator;
import com.yilnz.surfing.core.header.generators.CurlHeaderGenerator;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;
import com.yilnz.surfing.core.proxy.ippool.StandardIPPoolProvider;

import java.util.List;

public class HideMyIPPoolProvider extends StandardIPPoolProvider {
    public HideMyIPPoolProvider() {
        super( ".proxy__t");
    }

    @Override
    public String getPagedURL(int page) {
        return "https://hidemy.name/en/proxy-list/";
    }

    @Override
    public ProxyProvider getProxyProvider() {
        return new ProxyProvider() {
            @Override
            public HttpProxy getProxy() {
                return new HttpProxy("127.0.0.1", 7777);
            }
        };
    }

    @Override
    public SurfHttpRequest getCustomRequest() {
        final SurfHttpRequest request = new SurfHttpRequest();
        request.setMethod("GET");
        request.setConnectTimeout(6000);
        request.setHeaderGenerator(new CurlHeaderGenerator("curl 'https://hidemy.name/en/proxy-list/' -H 'authority: hidemy.name' -H 'upgrade-insecure-requests: 1' -H 'user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36' -H 'accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3' -H 'sec-fetch-site: same-origin' -H 'sec-fetch-mode: navigate' -H 'referer: https://hidemy.name/en/proxy-list/?start=64' -H 'accept-encoding: gzip, deflate, br' -H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8' -H 'cookie: cf_clearance=03661b79523f36eb89c54b6e60b79bfb3407dd3e-1575620499-0-150; __cfduid=dbd03e9fc30f4a9edf16436a60b554de61575620499; t=147872238; _ga=GA1.2.1413095784.1575620506; _gid=GA1.2.808937214.1575620506; PAPVisitorId=426fdf31a7240455005e791861AIio7F; PAPVisitorId=426fdf31a7240455005e791861AIio7F; _ym_uid=15756205121072232994; _ym_d=1575620512; _fbp=fb.1.1575620515629.553820755; _ym_isad=2; jv_enter_ts_EBSrukxUuA=1575620522222; jv_visits_count_EBSrukxUuA=1; PHPSESSID=0m9qsi8ojd8gck9pf570dl0622; _ym_wasSynced=%7B%22time%22%3A1575620609088%2C%22params%22%3A%7B%22eu%22%3A0%7D%2C%22bkParams%22%3A%7B%7D%7D; _ym_visorc_42065329=w; analytic_id=1575620619390; jv_pages_count_EBSrukxUuA=6; _gat_UA-90263203-1=1; _dc_gtm_UA-90263203-1=1' --compressed"));
        return request;
    }

    @Override
    public boolean isOverGFW() {
        return true;
    }

    @Override
    public List<HttpProxy> getProxyList() {
        final List<HttpProxy> proxyList = super.getProxyList();
        proxyList.forEach(e->{
            e.setOverGFW(true);
        });
        return proxyList;
    }

    public static void main(String[] args) {
        new HideMyIPPoolProvider().test();
    }
}
