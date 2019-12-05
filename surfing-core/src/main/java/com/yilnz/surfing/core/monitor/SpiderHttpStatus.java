package com.yilnz.surfing.core.monitor;

import com.yilnz.surfing.core.downloader.SurfHttpDownloader;

import java.util.Date;

public class SpiderHttpStatus implements SpiderHttpStatusMBean {

    private SurfHttpDownloader downloader;

    public SpiderHttpStatus(SurfHttpDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public int getTotalPageCount() {
        return downloader.getTotalPageCount().get();
    }

    @Override
    public int getSuccessPageCount() {
        return downloader.getSuccessPageCount().get();
    }

    @Override
    public int getErrorPageCount() {
        return downloader.getErrorPageCount().get();
    }

    @Override
    public Date getStartTime() {
        return downloader.getStartTime();
    }

    @Override
    public int getRetryPageCount() {
        return downloader.getRetryPageCount().get();
    }
}
