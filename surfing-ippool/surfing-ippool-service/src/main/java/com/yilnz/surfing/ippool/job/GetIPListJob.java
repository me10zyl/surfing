package com.yilnz.surfing.ippool.job;


import com.yilnz.surfing.ippool.service.impl.IPPoolService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

/**
 *  一分钟一次的获取IP操作
 */
@Component
@DisallowConcurrentExecution
public class GetIPListJob implements Job {

    private Logger logger = LoggerFactory.getLogger(GetIPListJob.class);

    @Autowired
    private IPPoolService ipPoolService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ipPoolService.setGFW(false);
        ipPoolService.doGetIPListWork();
    }
}
