package com.zingplay.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by thuydtm on 3:22 PM 7/9/2021
 */
@Document(collection = "statistical_game_log")
public class StatisticalGameLog {

    private String game;
    private Date time;
    private long numLog;

    public StatisticalGameLog(String game, Date time) {
        this.game = game;
        this.time = time;
        this.numLog = 0;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public long getNumLog() {
        return numLog;
    }

    public void setNumLog(long numLog) {
        this.numLog = numLog;
    }

    @Override
    public String toString() {
        return "{" +
                "time=" + time +
                ", numLog=" + numLog +
                '}';
    }
}
