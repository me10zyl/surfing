package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ippool.IPPool;
import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;

import java.util.List;
import java.util.Random;

public class IP3366IPPoolProvider implements IPPoolProvider {
    @Override
    public List<HttpProxy> getProxyList() {
        return IPPool.extractProxyListFromURL("http://www.ip3366.net/free/?stype=1&page=" + (new Random().nextInt(10) + 1), "#list");
    }

    public static void main(String[] args) {
        new IP3366IPPoolProvider().test();
    }
}
