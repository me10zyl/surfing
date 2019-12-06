package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;
import com.yilnz.surfing.core.proxy.ippool.StandardIPPoolProvider;

import java.util.List;

public class SSLProxiesIPPoolProvider extends StandardIPPoolProvider {
    public SSLProxiesIPPoolProvider() {
        super("#list");
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


    @Override
    public String getPagedURL(int page) {
        return "https://www.sslproxies.org/";
    }
}
