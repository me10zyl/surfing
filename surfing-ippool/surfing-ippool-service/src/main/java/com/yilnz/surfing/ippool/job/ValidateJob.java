package com.yilnz.surfing.ippool.job;


import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.ippool.service.impl.IPPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ValidateJob {

    private Logger logger = LoggerFactory.getLogger(ValidateJob.class);

    @Autowired
    private IPPoolService ipPoolService;

    public static final int MAX_VALIDATE_FAIL_COUNT = 7;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private ExecutorService validatePool = Executors.newFixedThreadPool(40);

    @PreDestroy
    private void destroy(){
        validatePool.shutdownNow();
    }

    @Scheduled(fixedDelay = 20000)
    public void validate() {
        final Set<HttpProxy> ipList = ipPoolService.getAllListFromRedis();
        List<Future<?>> futures = new ArrayList<>();
        ipList.forEach(e->{
            final Future<?> submit = validatePool.submit(() -> {
                try {
                    logger.info("验证{}", e);
                    final boolean validate = ipPoolService.validate(e);
                    if (!validate) {
                        ipPoolService.addErrorCountRedis2(e);
                        //次数大于阈值则删除
                        if (ipPoolService.getErrorCountRedis(e) >= MAX_VALIDATE_FAIL_COUNT) {
                            logger.info("代理不可用大于{}次，删除 {}", MAX_VALIDATE_FAIL_COUNT, e.toString());
                            ipPoolService.delFromRedis(e);
                           /* if(!ipPoolService.isRedisPoolFull()){
                                logger.info("删除代理之后，代理池不满，开始注入IP_LIST_2");
                                ipPoolService.injectIPListToRedis();
                            }*/
                        }
                    } else {
                        logger.info("代理{}验证通过，次数清零", e);
                        //验证通过，次数清0
                        ipPoolService.setErrorCountRedis(e, 0);
                    }
                }catch (Exception ee){
                    logger.error("验证失败了", ee);
                }
            });
            futures.add(submit);
        });
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.info("Future error", e);
            }
        }
    }
}
