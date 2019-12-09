package com.yilnz.surfing.core.proxy.ippool.iplist;

import com.yilnz.surfing.core.proxy.ippool.PagesIPPoolProvider;

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
