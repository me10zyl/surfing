package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ippool.IPPool;
import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;
import com.yilnz.surfing.core.proxy.ippool.PagesIPPoolProvider;

import java.util.List;
import java.util.Random;

public class KuaidailiIPPoolProvider extends PagesIPPoolProvider {

    public KuaidailiIPPoolProvider() {
        super(2, 3000, "#list");
    }

    @Override
    public String getPagedURL(int page) {
        return "https://www.kuaidaili.com/free/inha/" + page + "/";
    }

    public static void main(String[] args) {
        final List<HttpProxy> proxyList = new KuaidailiIPPoolProvider().getProxyList();
        System.out.println(proxyList.size());
        System.out.println(proxyList);
    }
}
