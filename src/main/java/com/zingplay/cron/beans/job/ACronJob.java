package com.zingplay.cron.beans.job;

import com.zingplay.cron.callback.CallBackJob;
import com.zingplay.cron.callback.ICallBack;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

public abstract class ACronJob implements Runnable {

    protected int id;
    protected int groupId;
    protected long timeStart;
    private AtomicLong processRecord;

    protected ICallBack callBack;

    public ACronJob(int id, int hashId, long timeStart) {
        this.id = id;
        this.groupId = hashId;
        this.timeStart = timeStart;
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public long getTimeStart() {
        return timeStart;
    }

    protected AtomicLong _getProcessRecord(){
        if(processRecord == null){
            processRecord = new AtomicLong();
        }
        return processRecord;
    }

    public long getProcessRecord() {
        return processRecord == null ? 0:processRecord.get();
    }


    public abstract int getStatus();

    public abstract String getIdJob();

    public abstract String getGame();

    public abstract String getCountry();

    @Override
    public void run() {
        callBack.onStart();
        doJob();
        callBack.onEnd();
    }

    protected abstract void doJob();

    public void initCallBack(Lock lock) {
        this.callBack = new CallBackJob(lock);
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
