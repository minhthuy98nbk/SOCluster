package com.zingplay.scheduler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CoreTaskScheduler {
    private static final int DEFAULT_POOL_SIZE = 1;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(DEFAULT_POOL_SIZE);

    private static CoreTaskScheduler instance = new CoreTaskScheduler();

    public static CoreTaskScheduler getInstance() {
        return instance;
    }

    public ScheduledFuture scheduleTask(Runnable task, int delay, int period, TimeUnit timeUnit) {
        return scheduledThreadPoolExecutor.scheduleAtFixedRate(task, delay, period, timeUnit);
    }
}
