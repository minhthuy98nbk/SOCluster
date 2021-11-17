package com.zingplay.models;

import com.zingplay.socket.v3.TypeParam;
import com.zingplay.socket.v3.TypeUpdateParam;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "user")
public class User {
    @Id
    private String id;
    @Indexed
    private int userId;
    private Date timeCreate;

    // check remove user
    private Date timeOnline;

    // biến trong map để quét object
    HashMap<String, ValueCondition> trackingObject;
    private HashMap<String,String> trackingStr;
    private HashMap<String,Long> trackingLong;
    private HashMap<String,Float> trackingFloat;
    private HashMap<String,Long> trackingDuration;

    //tuong lai xoa het ben dưới
    private int channelGame;
    private long totalGame;
    @Indexed
    private String game = "";
    @Indexed
    private String country = "";
    private int totalTimesPaid;
    private float totalPaid;
    private String lastPaidPack;
    private Set<String> channelPayments = new HashSet<>();

    public HashMap<String, ValueCondition> getTrackingObject() {
        return trackingObject;
    }

    public ValueCondition getTrackingObject(String key) {
        if(trackingObject != null){
            return trackingObject.get(key);
        }
        return null;
    }

    public String getTrackingStr(String key) {
        if(trackingStr != null){
            return trackingStr.get(key);
        }
        return null;
    }
    public Long getTrackingLong(String key, TypeParam typeLongParam) {
        switch (typeLongParam) {
            case LONG:
                if(trackingLong != null){
                    return trackingLong.get(key);
                }
                return null;
            case DURATION:
                if(trackingDuration != null){
                    return trackingDuration.get(key);
                }
                return null;
        }
        return null;
    }
    public Float getTrackingFloat(String key) {
        if(trackingFloat != null){
            return trackingFloat.get(key);
        }
        return null;
    }

    // object
    public void setTracking(String key, ValueCondition value){
        if(trackingObject == null) trackingObject = new HashMap<>();
        trackingObject.put(key,value);
    }
    // string
    public void setTracking(String key,String value){
        if(trackingStr == null) trackingStr = new HashMap<>();
        trackingStr.put(key,value);
    }
    // long
    public void setTracking(String key, Long value, TypeParam typeLongParam, TypeUpdateParam typeUpdateParam){
        switch (typeLongParam) {
            case LONG:
                setTracking(key, value, typeUpdateParam, trackingLong);
                break;
            case DURATION:
                setTracking(key, value, typeUpdateParam, trackingDuration);
                break;
        }
    }
    public void setTracking(String key, Long value, TypeUpdateParam typeUpdateParam, Map<String, Long> map){
        if(map == null) map = new HashMap<>();
        switch (typeUpdateParam) {
            case SET:
                map.put(key, value);
                return;
            case SUM:
                map.put(key, map.get(key) + value);
        }
    }
    // float
    public void setTracking(String key,Float value, TypeUpdateParam typeUpdateParam){
        if(trackingFloat == null) trackingFloat = new HashMap<>();
        switch (typeUpdateParam) {
            case SET:
                trackingFloat.put(key, value);
                return;
            case SUM:
                trackingFloat.put(key, trackingFloat.get(key) + value);
        }
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

    public HashMap<String, String> getTrackingStr() {
        return trackingStr;
    }

    public void setTrackingFloat(HashMap<String, Float> trackingFloat) {
        this.trackingFloat = trackingFloat;
    }

    public void setTrackingObject(HashMap<String, ValueCondition> trackingObject) {
        this.trackingObject = trackingObject;
    }

    public HashMap<String, Long> getTrackingDuration() {
        return trackingDuration;
    }

    public void setTrackingDuration(HashMap<String, Long> trackingDuration) {
        this.trackingDuration = trackingDuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return String.valueOf(userId);
    }

    public void setUserId(String userId) {
        this.userId = Integer.parseInt(userId);
    }

    public Set<String> getChannelPayments() {
        return channelPayments;
    }

    public void setChannelPayments(Set<String> channelPayments) {
        this.channelPayments = channelPayments;
    }

    public void addChannelPayment(String channelPayment){
        if(channelPayments == null){
            channelPayments = new HashSet<>();
        }
        channelPayments.add(channelPayment);
    }

    public Integer getChannelGame() {
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

    public Float getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(float totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getLastPaidPack() {
        return lastPaidPack;
    }

    public void setLastPaidPack(String lastPaidPack) {
        this.lastPaidPack = lastPaidPack;
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

    public Integer getTotalTimesPaid() {
        return totalTimesPaid;
    }

    public void setTotalTimesPaid(int totalTimesPaid) {
        this.totalTimesPaid = totalTimesPaid;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", channelGame=" + channelGame +
                ", totalGame=" + totalGame +
                ", timeCreate=" + timeCreate +
                ", timeOnline=" + timeOnline +
                ", game='" + game + '\'' +
                ", country='" + country + '\'' +
                ", totalTimesPaid=" + totalTimesPaid +
                ", totalPaid=" + totalPaid +
                ", lastPaidPack='" + lastPaidPack + '\'' +
                ", channelPayments=" + channelPayments +
                ", trackingStr=" + trackingStr +
                ", trackingFloat=" + trackingFloat +
                ", trackingLong=" + trackingLong +
                ", trackingObject=" + trackingObject +
                ", trackingDuration=" + trackingDuration +
                '}';
    }

    public void setUserId(int i) {
        this.userId = i;
    }

    public boolean isCustomData() {
        return trackingFloat != null ||
                trackingLong !=null ||
                trackingObject != null ||
                trackingStr != null ||
                trackingDuration !=null;
    }
}
