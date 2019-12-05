package com.yilnz.surfing.ippool.job;


import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;
import com.yilnz.surfing.ippool.service.impl.IPPoolService;
import com.yilnz.surfing.ippool.util.DistributeLock;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 *  一分钟一次的获取IP操作
 */
@Component
public class GetIPListJob {

    private Logger logger = LoggerFactory.getLogger(GetIPListJob.class);

    @Autowired
    private DistributeLock lock;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private List<IPPoolProvider> ipPoolProviderList;

    @Autowired
    private IPPoolService ipPoolService;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void getIPList() {
        if (!ipPoolService.isRedisPoolFull()) {
            logger.info("开始注入IP_LIST_1");
            ipPoolService.injectIPListToRedis();
        }
    }
}
