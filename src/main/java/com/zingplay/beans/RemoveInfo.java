package com.zingplay.beans;

import com.zingplay.enums.Config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thuydtm on 9/30/2021.
 */
public class RemoveInfo {
    // config
    private Date timeStartRemove;
    private Date lastTimeOnline;
    private int numRemovePerJob;
    private long minuteDelay;
    // first time
    private long numUserCountFirstTime;
    // count job
    private long numUserRemoved;
    private Map<Date, Integer> jobInfo;
    //
    private long curOldUser = 0;
    private int scheduleDelay;

    private int status;

    public RemoveInfo(Date timeStartRemove, Date lastTimeOnline, int numRemovePerJob, long minuteDelay, int scheduleDelay) {
        this.timeStartRemove = timeStartRemove;
        this.lastTimeOnline = lastTimeOnline;
        this.numRemovePerJob = numRemovePerJob;
        this.minuteDelay = minuteDelay;
        this.jobInfo = new HashMap<>();
        this.numUserRemoved = 0;
        this.scheduleDelay = scheduleDelay;
    }

    @Override
    public String toString() {
        return "RemoveInfo{" +
                "timeStartRemove=" + timeStartRemove +
                ", lastTimeOnline=" + lastTimeOnline +
                ", numRemovePerJob=" + numRemovePerJob +
                ", numUserCountFirstTime=" + numUserCountFirstTime +
                ", numUserRemoved=" + numUserRemoved +
                ", timeRemove=" + jobInfo +
                ", curOldUser=" + curOldUser +
                '}';
    }

    public Date getTimeStartRemove() {
        return timeStartRemove;
    }

    public void setTimeStartRemove(Date timeStartRemove) {
        this.timeStartRemove = timeStartRemove;
    }

    public Date getLastTimeOnline() {
        return lastTimeOnline;
    }

    public void setLastTimeOnline(Date lastTimeOnline) {
        this.lastTimeOnline = lastTimeOnline;
    }

    public int getNumRemovePerJob() {
        return numRemovePerJob;
    }

    public void setNumRemovePerJob(int numRemovePerJob) {
        this.numRemovePerJob = numRemovePerJob;
    }

    public long getNumUserRemoved() {
        return numUserRemoved;
    }

    public void setNumUserRemoved(long numUserRemoved) {
        this.numUserRemoved = numUserRemoved;
    }

    public long getNumUserCountFirstTime() {
        return numUserCountFirstTime;
    }

    public void setNumUserCountFirstTime(long numUserCountFirstTime) {
        this.numUserCountFirstTime = numUserCountFirstTime;
    }

    public Map<Date, Integer> getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(Map<Date, Integer> jobInfo) {
        this.jobInfo = jobInfo;
    }

    public long getMinuteDelay() {
        return minuteDelay;
    }

    public void setMinuteDelay(long minuteDelay) {
        this.minuteDelay = minuteDelay;
    }

    public long getCurOldUser() {
        return curOldUser;
    }

    public void setCurOldUser(long curOldUser) {
        this.curOldUser = curOldUser;
    }

    public int getScheduleDelay() {
        return scheduleDelay;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
