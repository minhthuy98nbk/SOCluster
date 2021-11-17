package com.zingplay.service;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Configuration
@EnableScheduling
@ComponentScan
@Component
public class CentralScheduler {
    private static AnnotationConfigApplicationContext CONEXT = null;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    public static CentralScheduler getInstance(){
        if( ! isValidBean()) {
            CONEXT = new AnnotationConfigApplicationContext(CentralScheduler.class);
        }
        return CONEXT.getBean(CentralScheduler.class);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(){
        return new ThreadPoolTaskScheduler();
    }

    public void start(Runnable task, String scheduleExpression) throws Exception {
        scheduler.schedule(task, new CronTrigger(scheduleExpression));
    }

    public void start(Runnable task, Long delay) throws Exception {
        scheduler.scheduleWithFixedDelay(task,delay);
    }

    public void stopAll(){
        scheduler.shutdown();
        CONEXT.close();
    }
    private static boolean isValidBean(){
        if( CONEXT == null || !CONEXT.isActive()){
            return false;
        }
        try {
            CONEXT.getBean(CentralScheduler.class);
        } catch (NoSuchBeanDefinitionException ex){
            return false;
        }
        return true;
    }
}
