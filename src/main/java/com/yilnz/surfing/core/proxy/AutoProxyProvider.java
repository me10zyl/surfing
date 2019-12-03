package com.yilnz.surfing.core.proxy;

import java.util.Arrays;
import java.util.List;

public class AutoProxyProvider extends HighAvailabilityProxyProvider {

    public AutoProxyProvider() {
        super(null);
        final List<HttpProxy> proxyList = IPPool.getProxyList();
        this.proxyList.addAll(proxyList);
        this.reservedProxyList.addAll(proxyList);
    }

    public AutoProxyProvider(List<HttpProxy> proxyList, boolean overwrite) {
        super(null);
        List<HttpProxy> cachedProxyList = proxyList;
        if (overwrite) {
            IPPool.saveCachedProxyList(proxyList, null, null);
        }else {
            cachedProxyList = IPPool.getCachedProxyListWithLog();
            if (cachedProxyList == null || cachedProxyList.size() == 0) {
                cachedProxyList = proxyList;
            } else {
                IPPool.saveCachedProxyList(proxyList, null, null);
            }
        }
        this.proxyList.addAll(cachedProxyList);
        this.reservedProxyList.addAll(cachedProxyList);
    }

    public AutoProxyProvider(List<HttpProxy> proxyList) {
        this(proxyList, false);
    }


    @Override
    protected void onRemoveProxy(HttpProxy removed) {
        final List<HttpProxy> a1 = Arrays.asList(usedProxyList.toArray(new HttpProxy[0]));
        final List<HttpProxy> a2 = Arrays.asList(reservedProxyList.toArray(new HttpProxy[0]));
        final List<HttpProxy> a3 = Arrays.asList(removedProxyList.toArray(new HttpProxy[0]));
        IPPool.saveCachedProxyList(a1, a2, a3);
    }
}
