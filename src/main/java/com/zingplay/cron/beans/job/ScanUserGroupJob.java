package com.zingplay.cron.beans.job;

import com.zingplay.cron.beans.group_jobs.GroupId;
import com.zingplay.models.ScheduleObject;
import com.zingplay.service.user.ScheduleObjectService;

import java.util.concurrent.atomic.AtomicInteger;

public class ScanUserGroupJob extends ACronJob {
    private static AtomicInteger atomicInteger = new AtomicInteger();
    private String idScheduleObject;
    private ScheduleObject scheduleObject;


    public ScanUserGroupJob(long timeStart) {
        super(atomicInteger.getAndIncrement(), GroupId.USER_GROUP.getCode(), timeStart);
    }

    @Override
    public int getStatus() {
        return scheduleObject == null ? -1: scheduleObject.getStatus();
    }

    @Override
    public String getIdJob() {
        return idScheduleObject;
    }

    @Override
    public String getGame() {
        return scheduleObject.getGame();
    }

    @Override
    public String getCountry() {
        return scheduleObject.getCountry();
    }

    @Override
    protected void doJob() {
        ScheduleObjectService.getInstance().scanObject(idScheduleObject,scheduleObject, _getProcessRecord());
        ScheduleObjectService.getInstance().createScheduleObjectSequence(idScheduleObject, scheduleObject);
    }

    public void setIdScheduleObject(String idScheduleObject) {
        this.idScheduleObject = idScheduleObject;
    }

    public ScheduleObject getScheduleObject() {
        return scheduleObject;
    }

    public void setScheduleObject(ScheduleObject scheduleObject) {
        this.scheduleObject = scheduleObject;
    }
}
