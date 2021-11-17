package com.zingplay.socket.v1.request;

import com.google.gson.Gson;
import com.zingplay.kafka.KafkaConsumerController;
import com.zingplay.kafka.KafkaSendingWorker;
import com.zingplay.service.user.UserService;
import com.zingplay.socket.SocketConst;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketMessageParser;
import com.zingplay.socket.SocketRequest;
import com.zingplay.socket.v1.response.UserOffers;

public class UserRequestOffer extends SocketRequest {
    private String userId;
    private long curTime;

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

    @Override
    public void execute(SocketInfo info, UserService userService) {
        String game = info.getGame();
        String country = info.getCountry();
        UserOffers offers = userService.getOffers(userId, game, country, getCurTime());
        KafkaSendingWorker.getInstance().send(info.getTopic(), SocketConst.ACTION_OFFER,new Gson().toJson(offers));
    }
}
