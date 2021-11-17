package com.zingplay.socket.v1.response;

import java.io.Serializable;
import java.util.List;

public class UserOffers implements Serializable {
    private String version = "1.0";
    private String userId;
    private List<Offer> offers;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}
