package com.zingplay.socket.v3;

import com.zingplay.helpers.Helpers;
import com.zingplay.models.ValueCondition;
import com.zingplay.service.user.UserService;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketRequest;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class UserTrackingCustom extends SocketRequest implements Serializable {
    private String userId;
    private long timeCreate;
    private long timeCurrent;
    private HashMap<String, ValueCondition> trackingObject;
    private HashMap<String,String> trackingStr;
    private HashMap<String,Long> trackingLong;
    private HashMap<String,Float> trackingFloat;
    private HashMap<String,Long> trackingDuration;

    public HashMap<String, String> getTrackingStr() {
        return trackingStr;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public ValueCondition getTrackingObject(String key) {
        return trackingObject.getOrDefault(key, null);
    }

    public String getTrackingStr(String key) {
        if(trackingStr != null){
            return trackingStr.get(key);
        }
        return null;
    }
    public Long getTrackingLong(String key) {
        if(trackingLong != null){
            return trackingLong.get(key);
        }
        return null;
    }
    public Float getTrackingFloat(String key) {
        if(trackingFloat != null){
            return trackingFloat.get(key);
        }
        return null;
    }

    public void setTracking(String key, ValueCondition value){
        if(trackingObject == null) trackingObject = new HashMap<>();
        trackingObject.put(key,value);
    }
    public void setTracking(String key,String value){
        if(trackingStr == null) trackingStr = new HashMap<>();
        trackingStr.put(key,value);
    }
    public void setTracking(String key,Long value){
        if(trackingLong == null) trackingLong = new HashMap<>();
        trackingLong.put(key,value);
    }
    public void setTracking(String key,Float value){
        if(trackingFloat == null) trackingFloat = new HashMap<>();
        trackingFloat.put(key,value);
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

    public HashMap<String, ValueCondition> getTrackingObject() {
        return trackingObject;
    }

    public void setTrackingObject(HashMap<String, ValueCondition> trackingObject) {
        this.trackingObject = trackingObject;
    }

    public Date getTimeCreate() {
        return new Date(Helpers.getLongTime(timeCreate));
    }

    public long getTimeCurrentLong() {
        return timeCurrent;
    }

    public void setTimeCreate(long timeCreate) {
        this.timeCreate = timeCreate;
    }

    public void setTimeCurrent(long timeCurrent) {
        this.timeCurrent = timeCurrent;
    }

    public long getTimeCurrent() {
        return timeCurrent;
    }

    public HashMap<String, Long> getTrackingDuration() {
        return trackingDuration;
    }

    public void setTrackingDuration(HashMap<String, Long> trackingDuration) {
        this.trackingDuration = trackingDuration;
    }

    @Override
    public void execute(SocketInfo info, UserService userService) {
        userService.logTracking(this,info.getGame(),info.getCountry());
    }

    @Override
    public String toString() {
        return "UserTrackingCustom{" +
                "userId='" + userId + '\'' +
                ", trackingObject=" + trackingObject +
                ", trackingStr=" + trackingStr +
                ", trackingLong=" + trackingLong +
                ", trackingFloat=" + trackingFloat +
                '}';
    }
}
