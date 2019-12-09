package com.yilnz.surfing.ippool.job;


import com.yilnz.surfing.ippool.service.impl.IPPoolService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
