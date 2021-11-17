package com.zingplay.socket.v3.request;

import com.google.gson.Gson;
import com.zingplay.kafka.KafkaSendingWorker;
import com.zingplay.service.user.UserService;
import com.zingplay.socket.SocketConst;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketRequest;
import com.zingplay.socket.v3.response.UserDataCustom;

public class UserRequestDataCustom extends SocketRequest {
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
        UserDataCustom userDataCustom = new UserDataCustom(this.userId);
        // add data gift
        userDataCustom.getDataCustoms().addAll(userService.getDataGift(userId, game, country, getCurTime()));
        // add other data custom
        // ...
        KafkaSendingWorker.getInstance().send(info.getTopic(), SocketConst.ACTION_GET_DATA_CUSTOM, new Gson().toJson(userDataCustom));
    }
}
