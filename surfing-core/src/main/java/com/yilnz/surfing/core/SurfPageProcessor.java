package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Page;

public abstract class SurfPageProcessor implements SurfPageProcessorInterface {

    @Override
    public Site getSite(){
        return Site.me();
    }

    public abstract void process(Page page);

    @Override
    public void processError(Page page){

    }
}
