package com.yilnz.surfing.ippool.config;

import com.yilnz.surfing.ippool.util.DistributeLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
@EnableAsync
public class ScheduleConfig {

    @Bean(destroyMethod = "release")
    @Scope(scopeName = "prototype")
    public DistributeLock distributeLock(){
        return new DistributeLock();
    }
}
