package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Page;

public interface SurfPageProcessorInterface {

    void process(Page page);

    void processError(Page page);
}
