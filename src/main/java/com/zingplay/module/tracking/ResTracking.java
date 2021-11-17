package com.zingplay.module.tracking;

import com.zingplay.socket.SocketConst;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thuydtm on 9:13 AM 7/15/2021
 */
public class ResTracking {
    private String id;
    private String game;
    private String country;
    private String date;
    private HashMap<String, Long> countLog;

    public ResTracking(String id, String game, String country, String date, HashMap<String, Long> countLog) {
        this.id = id;
        this.game = game;
        this.country = country;
        this.date = date;
        this.countLog = countLog;
    }

    public ResTracking(Tracking tracking) {
        this.id = tracking.getId();
        this.game = tracking.getGame();
        this.country = tracking.getCountry();
        this.date = tracking.getDate();
        this.countLog = new HashMap<>();
    }

    public ResTracking(Tracking tracking, String date, HashMap<String, Long> actionLogs) {
        this.id = tracking.getId();
        this.game = tracking.getGame();
        this.country = tracking.getCountry();
        this.date = date;
        this.countLog = actionLogs;
    }

//    public ResTracking(Tracking tracking, String date, HashMap<String, long[]> actionLogs, int idxPart) {
//        this.id = tracking.getId();
//        this.game = tracking.getGame();
//        this.country = tracking.getCountry();
//        this.date = date;
//        this.countLogin = getNumLogRaw(actionLogs, SocketConst.ACTION_LOGIN, idxPart);
//        this.countState = getNumLogRaw(actionLogs, SocketConst.ACTION_STATS_GAME, idxPart);
//        this.countPayment = getNumLogRaw(actionLogs, SocketConst.ACTION_USER_PAYMENT, idxPart);
//        this.countRequest = getNumLogRaw(actionLogs, SocketConst.ACTION_USER_REQUEST_OFFERS, idxPart);
//        this.countBuy = getNumLogRaw(actionLogs, SocketConst.ACTION_USER_BUY_OFFER, idxPart);
//        this.total = this.countLogin + this.countState + this.countPayment + this.countRequest + this.countBuy;
//    }

    @Override
    public String toString() {
        return "ResTracking{" +
                "id='" + id + '\'' +
                ", game='" + game + '\'' +
                ", country='" + country + '\'' +
                ", date='" + date + '\'' +
                ", countLog=" + countLog +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HashMap<String, Long> getCountLog() {
        return countLog;
    }

    public void setCountLog(HashMap<String, Long> countLog) {
        this.countLog = countLog;
    }
}
