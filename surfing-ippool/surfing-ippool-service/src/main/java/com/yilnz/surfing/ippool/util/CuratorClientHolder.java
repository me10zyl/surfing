package com.yilnz.surfing.ippool.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:zookeeper.properties")
public class CuratorClientHolder {
    @Value("${zk.registry.address}")
    private String mutexRegistryAdress;
    private CuratorFramework client;

    public CuratorFramework getClient(){
        if (this.client != null) {
            return this.client;
        }
        String registerAddr = mutexRegistryAdress.substring(12);
        CuratorFramework client = CuratorFrameworkFactory.newClient(registerAddr,
                new ExponentialBackoffRetry(1000, 3));
        client.start();
        this.client = client;
        return client;
    }
}
