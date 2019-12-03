package com.yilnz.surfing.core.proxy;

import com.yilnz.surfing.core.basic.Page;

public interface ProxyProvider {
    HttpProxy getProxy();
    default void pageReturn(HttpProxy proxy, Page page){
    }
}
