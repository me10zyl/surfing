package com.yilnz.surfing.ippool.service.impl;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.header.generators.ChromeHeaderGenerator;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Scope(scopeName = "prototype")
public class IPPoolService {

    @Autowired
    private List<IPPoolProvider> ipPoolProviderList;
    private ExecutorService validatePool = Executors.newCachedThreadPool();
    private ExecutorService ipWebsitePool = Executors.newFixedThreadPool(25);
    @Autowired
    private StringRedisTemplate redisTemplate;
    private String REDIS_KEY = "PROXY_IP_POOL";
    private String REDIS_KEY_2 = "PROXY_IP_POOL_2";
    private boolean isGFW = false;

    public static final int MAX_IP_COUNT = 100;
    private static final Logger logger = LoggerFactory.getLogger(IPPoolService.class);
    private int sequence = 0;

    public static final int MAX_VALIDATE_FAIL_COUNT = 7;

    private ExecutorService validatePool2 = Executors.newFixedThreadPool(40);

    @PreDestroy
    private void destroy2(){
        validatePool2.shutdownNow();
    }
    
    public void doGetIPListWork(){
        if (!this.isRedisPoolFull()) {
            logger.info("开始注入IP_LIST_1 GFW:{}", isGFW);
            this.injectIPListToRedis();
        }
    } 
    
    public void doValidateJob(){
        final Set<HttpProxy> ipList = this.getAllListFromRedis();
        List<Future<?>> futures = new ArrayList<>();
        ipList.forEach(e->{
            final Future<?> submit = validatePool2.submit(() -> {
                try {
                    //logger.info("验证{}", e);
                    final boolean validate = this.validate(e);
                    if (!validate) {
                        this.addErrorCountRedis2(e);
                        //次数大于阈值则删除
                        if (this.getErrorCountRedis(e) >= MAX_VALIDATE_FAIL_COUNT) {
                            logger.info("代理不可用大于{}次，删除 {}", MAX_VALIDATE_FAIL_COUNT, e.toString());
                            this.delFromRedis(e);
                        }
                    } else {
                        //logger.info("代理{}验证通过，次数清零", e);
                        //验证通过，次数清0
                        this.clearErrorCountRedis(e);
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
                logger.info("Future ERROR", e);
            }
        }
    }

   public void setGFW(boolean gfw){
       if (gfw) {
           REDIS_KEY = "PROXY_IP_POOL_GFW";
           REDIS_KEY_2 = "PROXY_IP_POOL_GFW_2";
           isGFW = true;
           ipPoolProviderList = ipPoolProviderList.stream().filter(IPPoolProvider::isOverGFW).collect(Collectors.toList());
       }else{
           isGFW = false;
           ipPoolProviderList = ipPoolProviderList.stream().filter(e->!e.isOverGFW()).collect(Collectors.toList());
       }
    }

    public void injectIPListToRedis() {
        List<Future<?>> ipGetFutrues = new ArrayList<>();
        for (IPPoolProvider ipPoolProvider : ipPoolProviderList) {
            final Future<?> ipGetFuture = ipWebsitePool.submit(() -> {
                final List<HttpProxy> proxyList = ipPoolProvider.getProxyList();
                logger.info("{}从{}中获取到了{}个IP", isGFW ? "[GFW]" : "", ipPoolProvider.getClass().getSimpleName(), proxyList.size());
                List<Future<Integer>> successSize = new ArrayList<>();
                for (HttpProxy httpProxy : proxyList) {
                    final Future<Integer> submit = validatePool.submit(() -> {
                        final Set<HttpProxy> allListFromRedis = getAllListFromRedis();
                        if (!allListFromRedis.contains(httpProxy) && validate(httpProxy)) {
                            pushToRedis(httpProxy);
                            return 2;
                        }
                        if (getListFromRedis().contains(httpProxy)) {
                            return 1;
                        }
                        return 0;

                    });
                    successSize.add(submit);
                }
                int successCount = 0;
                int newAddedCount = 0;
                for (Future<Integer> booleanFuture : successSize) {
                    try {
                        final Integer aBoolean = booleanFuture.get(5, TimeUnit.MINUTES);
                        if (aBoolean != null && aBoolean > 0) {
                            successCount++;
                            if (aBoolean == 2) {
                                newAddedCount++;
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Futrue执行出错", e);
                    } catch (TimeoutException e) {
                        logger.info("验证太久，放弃了{}", ipPoolProvider.getClass().getSimpleName());
                    }
                }
                if (proxyList.size() > 0) {
                    logger.info("{}筛选结果：{}中有{}个IP可用，其中新增{}个",isGFW ? "[GFW]" : "", ipPoolProvider.getClass().getSimpleName(), successCount, newAddedCount);
                }
            });
            ipGetFutrues.add(ipGetFuture);
        }
        for (Future<?> ipGetFutrue : ipGetFutrues) {
            try {
                ipGetFutrue.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Futrue执行出错2", e);
            }
        }
    }

    public static void main(String[] args) {
        final ThreadPoolExecutorFactoryBean threadFactory = new ThreadPoolExecutorFactoryBean();
        //threadFactory.setDaemon(true);
        final ExecutorService executorService = Executors.newFixedThreadPool(1, threadFactory);
        executorService.submit(()->{
            Thread.sleep(5000);
            System.out.println("hello");
           return 1;
        });
        executorService.submit(()->{
            Thread.sleep(5000);
            System.out.println("hello2");
            return 1;
        });
        System.out.println("hello end");
        executorService.shutdown();
        System.out.println("hello end2");
    }

    @PreDestroy
    private void destroy() {
        validatePool.shutdownNow();
        ipWebsitePool.shutdownNow();
    }

    public HttpProxy getOne() {
        if (getRedisIPListSize() == 0) {
            return null;
        }
        sequence = sequence % getRedisIPListSize();
        final HttpProxy httpProxy = redisTemplate.execute(new RedisCallback<HttpProxy>() {
            @Override
            public HttpProxy doInRedis(RedisConnection connection) throws DataAccessException {
                final Set<byte[]> bytes = connection.zRange(REDIS_KEY.getBytes(), sequence, sequence + 1);
                if (bytes != null && bytes.size() > 0) {
                    return HttpProxy.fromJSON(new String(bytes.iterator().next()));
                }
                return null;
            }
        });
        sequence++;
        return httpProxy;
    }

    public int getRedisIPListSize() {
        return redisTemplate.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection connection) throws DataAccessException {
                return Math.toIntExact(connection.zCard(REDIS_KEY.getBytes()));
            }
        });
    }

    public boolean isRedisPoolFull() {
        return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> redisConnection.zCard(REDIS_KEY.getBytes()) >= MAX_IP_COUNT);
    }

    public Set<HttpProxy> getListFromRedis() {
        return redisTemplate.execute(new RedisCallback<Set<HttpProxy>>() {
            @Override
            public Set<HttpProxy> doInRedis(RedisConnection connection) throws DataAccessException {
                final Set<HttpProxy> collect = connection.zRange(REDIS_KEY.getBytes(), 0, -1).stream().map(e -> {
                    return HttpProxy.fromJSON(new String(e));
                }).collect(Collectors.toSet());
                return collect;
            }
        });
    }

    public Set<HttpProxy> getAllListFromRedis() {
        final Set<HttpProxy> listFromRedis = getListFromRedis();
        listFromRedis.addAll(redisTemplate.execute(new RedisCallback<Set<HttpProxy>>() {
            @Override
            public Set<HttpProxy> doInRedis(RedisConnection connection) throws DataAccessException {
                final Set<HttpProxy> collect = connection.zRange(REDIS_KEY_2.getBytes(), 0, -1).stream().map(e -> {
                    return HttpProxy.fromJSON(new String(e));
                }).collect(Collectors.toSet());
                return collect;
            }
        }));
        return listFromRedis;
    }

    public boolean validate(HttpProxy httpProxy) {
        SurfHttpRequest request = new SurfHttpRequest("https://www.baidu.com");
        if (httpProxy.isOverGFW()) {
            request = new SurfHttpRequest("https://www.google.com");
        }
        request.setMethod("GET");
        request.setHeaderGenerator(new ChromeHeaderGenerator());
        request.setConnectTimeout(10000);
        final Page page = SurfSpider.create().addRequest(request).setProxy(httpProxy).request().get(0);
        if (page.getStatusCode() == 200) {
            return true;
        }
        return false;
    }

    public void pushToRedis(HttpProxy proxy) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.zAdd(REDIS_KEY.getBytes(), 0, proxy.toJSON().getBytes());
                return null;
            }
        });
    }

    public void delFromRedis(HttpProxy e) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.zRem(REDIS_KEY.getBytes(), e.toJSON().getBytes());
                connection.zRem(REDIS_KEY_2.getBytes(), e.toJSON().getBytes());
                return null;
            }
        });
    }

    public void addErrorCountRedis2(HttpProxy e) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.zRem(REDIS_KEY.getBytes(), e.toJSON().getBytes());
                connection.zIncrBy(REDIS_KEY_2.getBytes(), 1, e.toJSON().getBytes());
                return null;
            }
        });
    }

    public void clearErrorCountRedis(HttpProxy proxy) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                int score = 0;
                connection.zAdd(REDIS_KEY.getBytes(), score, proxy.toJSON().getBytes());
                connection.zRem(REDIS_KEY_2.getBytes(), proxy.toJSON().getBytes());
                return null;
            }
        });
    }

    public int getErrorCountRedis(HttpProxy proxy) {
        return redisTemplate.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection connection) throws DataAccessException {
                final Double aDouble = connection.zScore(REDIS_KEY_2.getBytes(), proxy.toJSON().getBytes());
                if (aDouble == null) {
                    return 0;
                }
                return (int) aDouble.doubleValue();
            }
        });
    }
}
