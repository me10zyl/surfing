package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ippool.IPPool;
import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;
import com.yilnz.surfing.core.proxy.ippool.PagesIPPoolProvider;

import java.util.List;

public class XiciIPPoolProvider extends PagesIPPoolProvider {
    public XiciIPPoolProvider() {
        super(5, 10, "#ip_list");
    }

    @Override
    public String getPagedURL(int page) {
        return "https://www.xicidaili.com/nn/" + page;
    }

    @Override
    public int[] getProxyFragmentIndex() {
        return new int[]{1,2,5};
    }

    public static void main(String[] args) {
        new XiciIPPoolProvider().test();
    }
}
