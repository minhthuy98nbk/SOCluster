package com.zingplay.socket.v3.request;

import com.zingplay.service.user.UserService;
import com.zingplay.socket.SocketConst;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketRequest;

public class UserReceiveDataCustom extends SocketRequest {
    private String userId;
    private String typeCustom;
    private String dataId;
    private long timeCurrent;
    private String country;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTypeCustom(String typeCustom) {
        this.typeCustom = typeCustom;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public void setTimeCurrent(long timeCurrent) {
        this.timeCurrent = timeCurrent;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserId() {
        return userId;
    }

    public String getTypeCustom() {
        return typeCustom;
    }

    public String getDataId() {
        return dataId;
    }

    public long getTimeCurrent() {
        return timeCurrent;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public void execute(SocketInfo info, UserService userService) {
        // System.out.println("execute handle data custom " + this.userId);
        String game = info.getGame();
        String country = info.getCountry();
        userService.logTrackingUserReceiveDataCustom(game, country, this);
    }

    @Override
    public String toString() {
        return "UserReceiveDataCustom{" +
                "userId='" + userId + '\'' +
                ", typeCustom='" + typeCustom + '\'' +
                ", dataId='" + dataId + '\'' +
                ", timeCurrent=" + timeCurrent +
                ", country='" + country + '\'' +
                '}';
    }
}
