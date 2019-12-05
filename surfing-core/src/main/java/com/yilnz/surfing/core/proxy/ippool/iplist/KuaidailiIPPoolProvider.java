package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ippool.IPPool;
import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;

import java.util.List;
import java.util.Random;

public class KuaidailiIPPoolProvider implements IPPoolProvider {
    @Override
    public List<HttpProxy> getProxyList() {
        return IPPool.extractProxyListFromURL("https://www.kuaidaili.com/free/inha/" + (new Random().nextInt(3000) + 1) + "/", "#list");
    }

    public static void main(String[] args) {
        new KuaidailiIPPoolProvider().test();
    }
}
