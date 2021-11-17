package com.zingplay.cron.callback;

import java.util.concurrent.locks.Lock;

public class CallBackJob implements ICallBack {
    private Lock lock;

    public CallBackJob(Lock lock) {
        this.lock = lock;
    }

    @Override
    public void onEnd() {
        lock.unlock();
    }

    @Override
    public void onStart() {
        lock.lock();
    }
}
