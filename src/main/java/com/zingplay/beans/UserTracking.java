package com.zingplay.beans;

import com.zingplay.models.ValueCondition;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class UserTracking implements Serializable {
    private String userId;

    private int channelGame;
    private long totalGame;

    private Date timeCreate;
    private Date timeOnline;

    private String game;
    private String country;

    private long totalPaid;
    private String lastPaidPack;
    private Set<String> channelPayments;

    private HashMap<String, ValueCondition> trackingObject;
    private HashMap<String,String> trackingStr;
    private HashMap<String,Long> trackingLong;
    private HashMap<String,Float> trackingFloat;
    private HashMap<String, Long> trackingDuration;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getChannelGame() {
        return channelGame;
    }

    public void setChannelGame(int channelGame) {
        this.channelGame = channelGame;
    }

    public long getTotalGame() {
        return totalGame;
    }

    public void setTotalGame(long totalGame) {
        this.totalGame = totalGame;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public Date getTimeOnline() {
        return timeOnline;
    }

    public void setTimeOnline(Date timeOnline) {
        this.timeOnline = timeOnline;
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

    public long getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(long totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getLastPaidPack() {
        return lastPaidPack;
    }

    public void setLastPaidPack(String lastPaidPack) {
        this.lastPaidPack = lastPaidPack;
    }

    public Set<String> getChannelPayments() {
        return channelPayments;
    }

    public void setChannelPayments(Set<String> channelPayments) {
        this.channelPayments = channelPayments;
    }

    public HashMap<String, ValueCondition> getTrackingObject() {
        return trackingObject;
    }

    public void setTrackingObject(HashMap<String, ValueCondition> trackingObject) {
        this.trackingObject = trackingObject;
    }

    public HashMap<String, String> getTrackingStr() {
        return trackingStr;
    }

    public void setTrackingStr(HashMap<String, String> trackingStr) {
        this.trackingStr = trackingStr;
    }

    public HashMap<String, Long> getTrackingLong() {
        return trackingLong;
    }

    public void setTrackingLong(HashMap<String, Long> trackingLong) {
        this.trackingLong = trackingLong;
    }

    public HashMap<String, Float> getTrackingFloat() {
        return trackingFloat;
    }

    public void setTrackingFloat(HashMap<String, Float> trackingFloat) {
        this.trackingFloat = trackingFloat;
    }

    public HashMap<String, Long> getTrackingDuration() {
        return trackingDuration;
    }

    public boolean isCustomData(){
        return trackingFloat != null ||
                trackingLong !=null ||
                trackingObject != null ||
                trackingStr != null ||
                trackingDuration !=null;
    }

    @Override
    public String toString() {
        return "UserTracking{" +
                "userId='" + userId + '\'' +
                ", channelGame=" + channelGame +
                ", totalGame=" + totalGame +
                ", timeCreate=" + timeCreate +
                ", timeOnline=" + timeOnline +
                ", game='" + game + '\'' +
                ", country='" + country + '\'' +
                ", totalPaid=" + totalPaid +
                ", lastPaidPack='" + lastPaidPack + '\'' +
                ", channelPayments=" + channelPayments +
                '}';
    }
}
