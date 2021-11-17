package com.zingplay.socket;

import com.zingplay.kafka.KafkaConsumerController;

public class SocketInfo {
    private String game;
    private String country;
    private String action;

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTopic(){
        return SocketConst.PREFIX_KEY + SocketConst.SEPARATOR_TOPIC + game + SocketConst.SEPARATOR_TOPIC + country;
    }
}
