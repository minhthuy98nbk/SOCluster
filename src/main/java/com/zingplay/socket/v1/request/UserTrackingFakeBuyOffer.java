package com.zingplay.socket.v1.request;

public class UserTrackingFakeBuyOffer {
    private String idUser;
    private String idOffer;
    private long timeCurrent;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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
}
