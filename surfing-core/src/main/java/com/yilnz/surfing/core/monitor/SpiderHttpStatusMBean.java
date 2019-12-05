package com.yilnz.surfing.core.monitor;

import java.util.Date;

public interface SpiderHttpStatusMBean {
    int getTotalPageCount();
    int getSuccessPageCount();
    int getErrorPageCount();
    Date getStartTime();
    int getRetryPageCount();
}
