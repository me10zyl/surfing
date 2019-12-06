package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ippool.IPPool;
import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;

import java.util.List;

import static com.yilnz.surfing.core.proxy.ippool.IPPool.extractProxyListFromURL;

public class XiciIPPoolProvider2 implements IPPoolProvider {
    @Override
    public List<HttpProxy> getProxyList() {
        return extractProxyListFromURL("https://www.xicidaili.com/nt/", "#ip_list", 1, 2, 5);
    }

    public static void main(String[] args) {
        new XiciIPPoolProvider2().test();
    }
}
