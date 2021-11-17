package com.zingplay.socket.v1.request;

import com.zingplay.service.user.UserService;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketRequest;

public class UserTrackingBuyOffer extends SocketRequest {
    private String userId;
    private String idOffer;
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

    public String getIdOffer() {
        return idOffer;
    }

    public void setIdOffer(String idOffer) {
        this.idOffer = idOffer;
    }

    @Override
    public void execute(SocketInfo info, UserService userService) {
        userService.logTrackingBuyOffer(this,info.getGame(),info.getCountry());
    }
}
