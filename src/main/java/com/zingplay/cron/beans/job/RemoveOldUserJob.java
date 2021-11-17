package com.zingplay.cron.beans.job;

import com.zingplay.cron.beans.group_jobs.GroupId;
import com.zingplay.service.user.RemoveService;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by thuydtm on 9/30/2021.
 */
public class RemoveOldUserJob extends ACronJob {
    private static AtomicInteger atomicInteger = new AtomicInteger();
    private Date timeStartRemove;
    private String gameId;
    private String country;
    private String username;

    public RemoveOldUserJob(long timeStart, Date timeStartRemove, String gameId, String country, String username) {
        super(atomicInteger.getAndIncrement(), GroupId.DELETE_USER.getCode(), timeStart);
        this.timeStartRemove = timeStartRemove;
        this.gameId = gameId;
        this.country = country;
        this.username = username;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getIdJob() {
        return null;
    }

    @Override
    public String getGame() {
        return null;
    }

    @Override
    public String getCountry() {
        return null;
    }

    @Override
    protected void doJob() {
        RemoveService.getInstance().removeAmountOldUser(timeStartRemove, gameId, country, username);
    }

    @Override
    public String toString() {
        return "RemoveOldUserJob{" +
                "timeStartRemove=" + timeStartRemove +
                '}';
    }

    public static AtomicInteger getAtomicInteger() {
        return atomicInteger;
    }

    public static void setAtomicInteger(AtomicInteger atomicInteger) {
        RemoveOldUserJob.atomicInteger = atomicInteger;
    }

}