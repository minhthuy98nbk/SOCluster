package com.zingplay.socket.v2.request;

import com.google.gson.Gson;
import com.zingplay.kafka.KafkaSendingWorker;
import com.zingplay.service.user.UserService;
import com.zingplay.socket.SocketConst;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketRequest;

public class UserRequestOfferDetail extends SocketRequest {
    private String idOffer;
    private String country;

    public String getIdOffer() {
        return idOffer;
    }

    public void setIdOffer(String idOffer) {
        this.idOffer = idOffer;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public void execute(SocketInfo info, UserService userService) {
        String game = info.getGame();
        com.zingplay.socket.v2.response.Offer offers = userService.getOffersDetail(getIdOffer(), game, info.getCountry(), country);
        KafkaSendingWorker.getInstance().send(info.getTopic(), SocketConst.ACTION_GET_OFFER,new Gson().toJson(offers));
    }
}
