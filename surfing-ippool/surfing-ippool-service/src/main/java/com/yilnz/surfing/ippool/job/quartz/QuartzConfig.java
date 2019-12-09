package com.yilnz.surfing.ippool.job.quartz;


import com.yilnz.surfing.ippool.job.GetIPListJob;
import com.yilnz.surfing.ippool.job.GetIPListJobGFW;
import com.yilnz.surfing.ippool.job.ValidateJob;
import com.yilnz.surfing.ippool.job.ValidateJobGFW;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail job1(){
        return JobBuilder.newJob(GetIPListJob.class).withIdentity("job1").storeDurably().build();
    }

    @Bean
    public JobDetail job2(){
        return JobBuilder.newJob(GetIPListJobGFW.class).withIdentity("job2").storeDurably().build();
    }

    @Bean
    public JobDetail job3(){
        return JobBuilder.newJob(ValidateJob.class).withIdentity("job3").storeDurably().build();
    }

    @Bean
    public JobDetail job4(){
        return JobBuilder.newJob(ValidateJobGFW.class).withIdentity("job4").storeDurably().build();
    }

    @Bean
    public Trigger trigger(@Qualifier("job1") JobDetail job){
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("trigger1")
                .withDescription("GetIPListJobTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(60))
                .build();
    }


    public Trigger trigger2(@Qualifier("job2") JobDetail job){
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("trigger2")
                .withDescription("GetIPListJobTrigger2")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(60))
                .build();
    }
    @Bean
    public Trigger trigger3(@Qualifier("job3") JobDetail job){
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("trigger3")
                .withDescription("ValidateJob trigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(20))
                .build();
    }

    public Trigger trigger4(@Qualifier("job4") JobDetail job){
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("trigger4")
                .withDescription("ValidateJob trigger2")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(20))
                .build();
    }
}
