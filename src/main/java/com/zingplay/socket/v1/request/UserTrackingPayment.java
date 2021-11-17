package com.zingplay.socket.v1.request;

import com.zingplay.service.user.UserService;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketRequest;

public class UserTrackingPayment extends SocketRequest {
    private String userId;
    private float packCost;
    private String channelPayment;
    private String country;
    private long timeCurrent;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimeCurrent() {
        return timeCurrent;
    }

    public void setTimeCurrent(long timeCurrent) {
        this.timeCurrent = timeCurrent;
    }

    public float getPackCost() {
        return packCost;
    }

    public void setPackCost(float packCost) {
        this.packCost = packCost;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getChannelPayment() {
        return channelPayment;
    }

    public void setChannelPayment(String channelPayment) {
        this.channelPayment = channelPayment;
    }

    @Override
    public void execute(SocketInfo info, UserService userService) {
        if(country == null){
            country = info.getCountry();
        }
        userService.logTrackingPayment(this,info.getGame(),country);
    }
}
