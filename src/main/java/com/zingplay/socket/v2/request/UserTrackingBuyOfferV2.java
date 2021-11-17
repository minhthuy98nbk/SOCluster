package com.zingplay.socket.v2.request;

import com.zingplay.service.user.UserService;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketRequest;

public class UserTrackingBuyOfferV2 extends SocketRequest {
    private String userId;
    private String idOffer;
    private String country;
    private float price;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public void execute(SocketInfo info, UserService userService) {
        String country = this.getCountry();
        if(country == null || country.isEmpty()){
            this.setCountry(info.getCountry());
        }

        userService.logTrackingBuyOfferV2(this,info.getGame(), this.getCountry());
    }
}
