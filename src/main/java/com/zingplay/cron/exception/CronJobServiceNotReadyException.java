package com.zingplay.cron.exception;

public class CronJobServiceNotReadyException extends CronJobException {

    public CronJobServiceNotReadyException() {
        super("cron job service not ready!");
    }

    public CronJobServiceNotReadyException(String msg) {
        super(msg);
    }
}
