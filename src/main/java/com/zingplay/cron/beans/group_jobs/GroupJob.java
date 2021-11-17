package com.zingplay.cron.beans.group_jobs;

import com.zingplay.cron.beans.job.ACronJob;
import com.zingplay.cron.comparator.JobComparator;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GroupJob {
    private int id;
    BlockingQueue<ACronJob> queueJob = new PriorityBlockingQueue<>(10, JobComparator.getInstance());
    Lock actionLock = new ReentrantLock();

    public GroupJob(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public BlockingQueue<ACronJob> getQueueJob() {
        return queueJob;
    }

    public Lock getActionLock() {
        return actionLock;
    }

    public void put(ACronJob cronJob) throws InterruptedException {
        queueJob.put(cronJob);
    }

    public int size() {
        return queueJob.size();
    }

    public ACronJob getFirstJob() {
        return queueJob.peek();
    }

    public ACronJob removeFirstJob() {
        return queueJob.poll();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean removeJob(int jobId) {
        boolean isRemoved = false;
        Iterator<ACronJob> iterator = queueJob.iterator();
        while (iterator.hasNext()) {
            ACronJob job = iterator.next();
            if(job.getId() == jobId) {
                iterator.remove();
                isRemoved = true;
            }
        }
        return isRemoved;
    }
}
