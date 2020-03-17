package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;
import com.yilnz.surfing.core.proxy.ippool.IPPool;
import com.yilnz.surfing.core.proxy.ippool.StandardIPPoolProvider;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FreeProxyCZIPPoolProvider extends StandardIPPoolProvider {
    public FreeProxyCZIPPoolProvider() {
        super( "#proxy_list");
    }

    @Override
    public String getPagedURL(int page) {
        return "http://free-proxy.cz/en/proxylist/country/all/http/ping/all";
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
        new FreeProxyCZIPPoolProvider().test();
    }

    @Override
    protected void extract(ArrayList<HttpProxy> all, int i) {
        final String pagedURL = getPagedURL(i);
        final SurfHttpRequest customRequest = getCustomRequest();
        customRequest.setConnectTimeout(10000);
        customRequest.setUrl(pagedURL);
        List<HttpProxy> httpProxies =  IPPool.extractProxyListFromURL(customRequest, getProxyProvider(), "#proxy_list", 0, 1,-1, new IPPool.TrHandler() {
            @Override
            public HttpProxy handleTr(Selectable tr, List<Selectable> tds) {
                if(tds.size() < 2){
                    return null;
                }
                final Selectable td0 = tds.get(0);
                final Selectable td1 = tds.get(1);
                final String ip = new String(Base64.getDecoder().decode(td0.select(Selectors.regex("Base64.decode\\(\"(.+)\"\\)", 1)).get()));
                final Selectable port = td1.select(Selectors.regex("\\d+", 0));
                return new HttpProxy(ip, Integer.parseInt(port.get()));
            }
        });
        all.addAll(httpProxies);
    }
}
