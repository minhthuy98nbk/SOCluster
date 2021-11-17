package com.zingplay.socket.v2.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Price implements Serializable {
    @SerializedName("c")
    String country;
    @SerializedName("o")
    long basePrice;
    @SerializedName("p")
    float price;
    @SerializedName("$")
    String currency;
    @SerializedName("b")
    int bonus;
    @SerializedName("s")
    Set<String> channels;

    public static Price from(com.zingplay.models.Price price) {
        if(price == null) return null;
        Price p = new Price();
        p.currency = price.getCurrency();
        p.country = price.getCountry();
        p.basePrice = price.getBase();
        p.price = price.getPrice();
        p.bonus = price.getBonus();
        Set<String> channels = price.getChannels();
        p.setChannels(channels == null?new HashSet<>():channels);
        p.setCurrency(price.getCurrency());
        return p;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(long basePrice) {
        this.basePrice = basePrice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public Set<String> getChannels() {
        return channels;
    }

    public void setChannels(Set<String> channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "Price{" +
                "country='" + country + '\'' +
                ", basePrice=" + basePrice +
                ", price=" + price +
                ", bonus=" + bonus +
                '}';
    }
}
