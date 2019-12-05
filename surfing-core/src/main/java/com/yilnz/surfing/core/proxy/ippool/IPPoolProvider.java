package com.yilnz.surfing.core.proxy.ippool;

import com.yilnz.surfing.core.proxy.HttpProxy;

import java.util.List;

public interface IPPoolProvider {
    List<HttpProxy> getProxyList();

    default void test(){
        final List<HttpProxy> proxyList = this.getProxyList();
        System.out.println(proxyList);
    }
}
