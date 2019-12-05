package com.yilnz.surfing.ippool.service.impl;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSprider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.header.generators.ChromeHeaderGenerator;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ippool.IPPoolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class IPPoolService {

    @Autowired
    private List<IPPoolProvider> ipPoolProviderList;
    private ExecutorService validatePool = Executors.newFixedThreadPool(50);
    private ExecutorService ipWebsitePool = Executors.newFixedThreadPool(25);
    @Autowired
    private StringRedisTemplate redisTemplate = new StringRedisTemplate();
    public static final String REDIS_KEY = "PROXY_IP_POOL";
    public static final String REDIS_KEY_2 = "PROXY_IP_POOL_2";
    public static final int MAX_IP_COUNT = 100;
    private static final Logger logger = LoggerFactory.getLogger(IPPoolService.class);
    private int sequence = 0;

    public void injectIPListToRedis() {
        List<Future<?>> ipGetFutrues = new ArrayList<>();
        for (IPPoolProvider ipPoolProvider : ipPoolProviderList) {
            final Future<?> ipGetFuture = ipWebsitePool.submit(() -> {
                final List<HttpProxy> proxyList = ipPoolProvider.getProxyList();
                logger.info("从{}中获取到{}个IP", ipPoolProvider.getClass().toString(), proxyList.size());
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
                        final Integer aBoolean = booleanFuture.get();
                        if (aBoolean != null && aBoolean > 0) {
                            successCount++;
                            if (aBoolean == 2) {
                                newAddedCount++;
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Futrue执行出错", e);
                    }
                }
                if (proxyList.size() > 0) {
                    logger.info("{}中有{}个IP可用，其中新增{}个", ipPoolProvider.getClass().toString(), successCount, newAddedCount);
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

    @PreDestroy
    private void destroy() {
        validatePool.shutdownNow();
    }

    public HttpProxy getOne() {
        sequence = sequence % getRedisIPListSize();
        final HttpProxy httpProxy = redisTemplate.execute(new RedisCallback<HttpProxy>() {
            @Override
            public HttpProxy doInRedis(RedisConnection connection) throws DataAccessException {
                final Set<byte[]> bytes = connection.zRange(REDIS_KEY.getBytes(), sequence, sequence + 1);
                if (bytes != null && bytes.size() > 0) {
                    return new HttpProxy(new String(bytes.iterator().next()));
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
                    return new HttpProxy(new String(e));
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
                    return new HttpProxy(new String(e));
                }).collect(Collectors.toSet());
                return collect;
            }
        }));
        return listFromRedis;
    }

    public boolean validate(HttpProxy httpProxy) {
        final SurfHttpRequest request = new SurfHttpRequest("https://www.baidu.com");
        request.setMethod("GET");
        request.setHeaderGenerator(new ChromeHeaderGenerator());
        request.setConnectTimeout(10000);
        final Page page = SurfSprider.create().addRequest(request).setProxy(httpProxy).request();
        if (page.getStatusCode() == 200) {
            return true;
        }
        return false;
    }

    public void pushToRedis(HttpProxy proxy) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.zAdd(REDIS_KEY.getBytes(), 0, proxy.toString().getBytes());
                return null;
            }
        });
    }

    public void delFromRedis(HttpProxy e) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.zRem(REDIS_KEY.getBytes(), e.toString().getBytes());
                connection.zRem(REDIS_KEY_2.getBytes(), e.toString().getBytes());
                return null;
            }
        });
    }

    public void addErrorCountRedis2(HttpProxy e) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.zRem(REDIS_KEY.getBytes(), e.toString().getBytes());
                connection.zIncrBy(REDIS_KEY_2.getBytes(), 1, e.toString().getBytes());
                return null;
            }
        });
    }

    public void setErrorCountRedis(HttpProxy proxy, int i) {
        if (i == 0) {
            redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.zAdd(REDIS_KEY.getBytes(), i, proxy.toString().getBytes());
                    connection.zRem(REDIS_KEY_2.getBytes(), proxy.toString().getBytes());
                    return null;
                }
            });
        } else {
            redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.zRem(REDIS_KEY.getBytes(), proxy.toString().getBytes());
                    connection.zAdd(REDIS_KEY_2.getBytes(), i, proxy.toString().getBytes());
                    return null;
                }
            });
        }

    }

    public int getErrorCountRedis(HttpProxy proxy) {
        return redisTemplate.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection connection) throws DataAccessException {
                final Double aDouble = connection.zScore(REDIS_KEY_2.getBytes(), proxy.toString().getBytes());
                if (aDouble == null) {
                    return 0;
                }
                return (int) aDouble.doubleValue();
            }
        });
    }
}
