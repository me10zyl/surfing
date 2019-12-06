package com.yilnz.surfing.ippool.job;


import com.yilnz.surfing.ippool.service.impl.IPPoolService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
public class GetIPListJobGFW implements Job {

    private Logger logger = LoggerFactory.getLogger(GetIPListJobGFW.class);

    @Autowired
    private IPPoolService ipPoolService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ipPoolService.setGFW(true);
        ipPoolService.doGetIPListWork();
    }
}
