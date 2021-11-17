package com.zingplay.service.alert;

import com.zingplay.models.AlertGameInfo;

/**
 * Created by thuydtm on 8:17 PM 7/15/2021
 */
public class ResAlertConfig {
    boolean isSuccess;
    String msg;
    AlertGameInfo alertGameInfo;

    public ResAlertConfig(boolean isSuccess, String msg, AlertGameInfo alertGameInfo) {
        this.isSuccess = isSuccess;
        this.msg = msg;
        this.alertGameInfo = alertGameInfo;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AlertGameInfo getAlertGameInfo() {
        return alertGameInfo;
    }

    public void setAlertGameInfo(AlertGameInfo alertGameInfo) {
        this.alertGameInfo = alertGameInfo;
    }
}
