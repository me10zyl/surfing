package com.yilnz.surfing.ippool.config;

import com.yilnz.surfing.core.proxy.ippool.iplist.IP66IPPoolProvider;
import com.yilnz.surfing.core.proxy.ippool.iplist.XiciIPPoolProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IPPoolProviderConfig {
    @Bean
    public IP66IPPoolProvider ip66(){
        return new IP66IPPoolProvider();
    }
    @Bean
    public XiciIPPoolProvider xici(){
        return new XiciIPPoolProvider();
    }
}
