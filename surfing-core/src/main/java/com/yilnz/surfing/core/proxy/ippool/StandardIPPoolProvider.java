package com.yilnz.surfing.core.proxy.ippool;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.header.generators.BulkHeaderGenerator;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public  abstract class StandardIPPoolProvider extends PagesIPPoolProvider {

    public StandardIPPoolProvider(String tableCssSelector) {
        super(-1, 1, tableCssSelector);
    }
}
