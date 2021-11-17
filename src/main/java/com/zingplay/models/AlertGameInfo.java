package com.zingplay.models;

import com.zingplay.service.alert.AlertGameService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by thuydtm on 3:22 PM 7/9/2021
 */

@Document(collection = "alert_game_info")
public class AlertGameInfo {
    @Id
    private String game;
    private float rateUp;
    private float rateDown;
    private long minuteCountLog;
    private long minutePastCheck;

    public AlertGameInfo(String game) {
        this.game = game;
        this.rateUp = AlertGameService.getInstance().defaultRateUp;
        this.rateDown = AlertGameService.getInstance().defaultRateDown;
        this.minuteCountLog = AlertGameService.getInstance().defaultMinuteCountLog;
        this.minutePastCheck = AlertGameService.getInstance().defaultMinutePastCheck;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public float getRateUp() {
        return rateUp;
    }

    public void setRateUp(float rateUp) {
        this.rateUp = rateUp;
    }

    public float getRateDown() {
        return rateDown;
    }

    public void setRateDown(float rateDown) {
        this.rateDown = rateDown;
    }

    public long getMinuteCountLog() {
        return minuteCountLog;
    }

    public void setMinuteCountLog(long minuteCountLog) {
        this.minuteCountLog = minuteCountLog;
    }

    public long getMinutePastCheck() {
        return minutePastCheck;
    }

    public void setMinutePastCheck(long minutePastCheck) {
        this.minutePastCheck = minutePastCheck;
    }

    @Override
    public String toString() {
        return "AlertGameInfo{" +
                "game='" + game + '\'' +
                ", rateUp=" + rateUp +
                ", rateDown=" + rateDown +
                ", minuteCountLog=" + minuteCountLog +
                ", minutePastCheck=" + minutePastCheck +
                '}';
    }
}
