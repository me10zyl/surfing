package com.yilnz.surfing.core.proxy;

import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.exception.NoProxyException;

import java.util.ArrayList;
import java.util.List;

public class LoadBalancingProxyProvider implements ProxyProvider {

    protected List<HttpProxy> proxyList = new ArrayList<>();
    protected int sequence = 0;

    public LoadBalancingProxyProvider(List<HttpProxy> proxyList) {
        if (proxyList != null && proxyList.size() == 0) {
            throw new NoProxyException("[surfing]没有任何可用的代理");
        }
        if (proxyList != null) {
            this.proxyList.addAll(proxyList);
        }
    }

    public List<HttpProxy> getProxyList() {
        return proxyList;
    }

    @Override
    public HttpProxy getProxy() {
        if (sequence >= proxyList.size()) {
            sequence = 0;
        }
        final HttpProxy httpProxy = proxyList.get(sequence);
        sequence++;
        return httpProxy;
    }

    @Override
    public void pageReturn(HttpProxy proxy, Page page) {

    }
}
