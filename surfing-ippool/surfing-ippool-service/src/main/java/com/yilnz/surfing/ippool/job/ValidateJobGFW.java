package com.yilnz.surfing.ippool.job;


import com.yilnz.surfing.core.proxy.HttpProxy;
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

import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 每5秒检查一次IP池，如果IP失效则删除，如果没满则插入IP
 */
@Component
@DisallowConcurrentExecution
public class ValidateJobGFW implements Job {

    private Logger logger = LoggerFactory.getLogger(ValidateJobGFW.class);

    @Autowired
    private IPPoolService ipPoolService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ipPoolService.setGFW(true);
        ipPoolService.doValidateJob();
    }
}
