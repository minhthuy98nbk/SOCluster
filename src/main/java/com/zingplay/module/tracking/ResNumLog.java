package com.zingplay.module.tracking;

/**
 * Created by thuydtm on 3:45 PM 7/14/2021
 */
public class ResNumLog {
    long numLog;
    int minuteCountLog;

    public ResNumLog(long numLog, int minuteCountLog) {
        this.numLog = numLog;
        this.minuteCountLog = minuteCountLog;
    }

    public long getNumLog() {
        return numLog;
    }

    public void setNumLog(long numLog) {
        this.numLog = numLog;
    }

    public int getMinuteCountLog() {
        return minuteCountLog;
    }

    public void setMinuteCountLog(int minuteCountLog) {
        this.minuteCountLog = minuteCountLog;
    }

    @Override
    public String toString() {
        return "ResNumLog{" +
                "numLog=" + numLog +
                ", minuteCountLog=" + minuteCountLog +
                '}';
    }
}
