package com.zingplay.cron.exception;

public class CronJobGroupIdInvalidException extends CronJobException {

    public CronJobGroupIdInvalidException() {
        super("cron job groupId invalid!");
    }

    public CronJobGroupIdInvalidException(String msg) {
        super(msg);
    }
}
