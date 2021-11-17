package com.zingplay.socket.v2.request;

import com.google.gson.Gson;
import com.zingplay.kafka.KafkaSendingWorker;
import com.zingplay.service.user.UserService;
import com.zingplay.socket.SocketConst;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketRequest;
import com.zingplay.socket.v1.response.UserOffers;

public class UserRequestOfferV2 extends SocketRequest {
    private String userId;
    private long curTime;
    private String country;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCurTime() {
        return curTime;
    }

    public void setCurTime(long curTime) {
        this.curTime = curTime;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public void execute(SocketInfo info, UserService userService) {
        String game = info.getGame();
        com.zingplay.socket.v2.response.UserOffers offers = userService.getOffersV2(userId, game, info.getCountry(), getCurTime(),country);
        KafkaSendingWorker.getInstance().send(info.getTopic(), SocketConst.ACTION_OFFER_LOCAL_PRICE_V2,new Gson().toJson(offers));
    }
}
