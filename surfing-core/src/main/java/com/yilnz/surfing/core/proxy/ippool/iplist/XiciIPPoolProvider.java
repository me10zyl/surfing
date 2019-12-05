package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ippool.IPPool;
import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;

import java.util.List;

public class XiciIPPoolProvider implements IPPoolProvider {
    @Override
    public List<HttpProxy> getProxyList() {
        return IPPool.getXici();
    }
}
