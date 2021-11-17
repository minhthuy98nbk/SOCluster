package com.zingplay.socket.v1.request;

import com.zingplay.service.user.UserService;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketRequest;

import java.io.Serializable;

public class UserTrackingLogin extends SocketRequest implements Serializable {
    private String userId;
    private long timeCreate;
    private long timeOnline;
    private int channelIdx;
    private long totalGame;
    private long timeCurrent;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(long timeCreate) {
        this.timeCreate = timeCreate;
    }

    public long getTimeOnline() {
        return timeOnline;
    }

    public void setTimeOnline(long timeOnline) {
        this.timeOnline = timeOnline;
    }

    public int getChannelIdx() {
        return channelIdx;
    }

    public void setChannelIdx(int channelIdx) {
        this.channelIdx = channelIdx;
    }

    public long getTotalGame() {
        return totalGame;
    }

    public void setTotalGame(long totalGame) {
        this.totalGame = totalGame;
    }

    public long getTimeCurrent() {
        return timeCurrent;
    }

    public void setTimeCurrent(long timeCurrent) {
        this.timeCurrent = timeCurrent;
    }

    @Override
    public void execute(SocketInfo info, UserService userService) {
        userService.logTrackingLogin(this,info.getGame(),info.getCountry());
    }
}
