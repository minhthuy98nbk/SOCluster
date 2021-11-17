package com.zingplay.module.tracking;

import com.zingplay.socket.SocketConst;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;

@Document(collection = "tracking")
public class Tracking {
    @Id
    private String id;

    @Indexed
    private String game;
    @Indexed
    private String country;
    @Indexed
    private String date;

    private long countLogin;
    private long countState;
    private long countPayment;
    private long countRequest;
    private long countDataCustom;
    private long countBuy;

    private Date timeCreate;

    // new
    private HashMap<Integer, HashMap<String, long[]>> countDetailLog;
    private HashMap<Integer, Integer> numPartByHour;

    // get
//    public long getActionLog(int hour, int minute, int idxPart){
//        actionLogs.get(SocketConst.ACTION_LOGIN)[idxPart]
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGame() {
        return game;
    }

    public String getCountry() {
        return country;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getCountLogin() {
        return countLogin;
    }

    public void setCountLogin(long countLogin) {
        this.countLogin = countLogin;
    }

    public long getCountState() {
        return countState;
    }

    public void setCountState(long countState) {
        this.countState = countState;
    }

    public long getCountRequest() {
        return countRequest;
    }

    public void setCountRequest(long countRequest) {
        this.countRequest = countRequest;
    }

    public long getCountBuy() {
        return countBuy;
    }

    public void setCountBuy(long countBuy) {
        this.countBuy = countBuy;
    }

    public long getCountPayment() {
        return countPayment;
    }

    public void setCountPayment(long countPayment) {
        this.countPayment = countPayment;
    }

    public HashMap<Integer, HashMap<String, long[]>> getCountDetailLog() {
        if(countDetailLog == null) countDetailLog = new HashMap<>();
        return countDetailLog;
    }

    public void setCountDetailLog(HashMap<Integer, HashMap<String, long[]>> countDetailLog) {
        this.countDetailLog = countDetailLog;
    }

    public HashMap<Integer, Integer> getNumPartByHour() {
        if(numPartByHour == null) numPartByHour = new HashMap<>();
        return numPartByHour;
    }

    public void setNumPartByHour(HashMap<Integer, Integer> numPartByHour) {
        this.numPartByHour = numPartByHour;
    }

    public String getKey(){
        return game + country + date;
    }

    public long getCountDataCustom() {
        return countDataCustom;
    }

    public void setCountDataCustom(long countDataCustom) {
        this.countDataCustom = countDataCustom;
    }
}
