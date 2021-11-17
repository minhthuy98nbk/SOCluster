package com.zingplay.cron;

import com.zingplay.cron.beans.job.ACronJob;
import com.zingplay.cron.exception.CronJobException;

public interface ICronJobService {
    // reload cron jobs from db
    //void wake();

    boolean addJob(ACronJob cronJob, boolean force) throws CronJobException;

    boolean removeJob(Integer jobId) throws CronJobException;

    boolean removeGroupJobs(int groupId) throws CronJobException;

    int countTotalJobs() throws CronJobException;

    int countGroupJobs(int groupId) throws CronJobException;

}
