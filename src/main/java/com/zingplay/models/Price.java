package com.zingplay.models;

import java.io.Serializable;
import java.util.Set;

public class Price implements Serializable {
    String country;
    String currency;
    long base;
    float price;
    int bonus;
    Set<String> channels;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Set<String> getChannels() {
        return channels;
    }

    public void setChannels(Set<String> channels) {
        this.channels = channels;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getBase() {
        return base;
    }

    public void setBase(long base) {
        this.base = base;
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
}
