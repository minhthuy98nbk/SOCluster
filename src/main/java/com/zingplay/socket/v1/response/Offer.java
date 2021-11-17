package com.zingplay.socket.v1.response;

import com.zingplay.models.Item;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Offer implements Serializable {
    private String id;
    private String name;
    private String idRunOffer;
    private int priority;

    private String country;
    private int basePrice;
    private float price;
    private int bonus;
    private int iconNum;
    private int themeNum;

    private long timeStart;
    private long timeEnd;

    private Set<Item> items;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public void setIconNum(int iconNum) {
        this.iconNum = iconNum;
    }

    public int getThemeNum() {
        return themeNum;
    }

    public void setThemeNum(int themeNum) {
        this.themeNum = themeNum;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart.getTime();
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd.getTime();
    }

    public int getBasePrice() {
        return basePrice;
    }

    public int getBonus() {
        return bonus;
    }

    public int getIconNum() {
        return iconNum;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public String getIdRunOffer() {
        return idRunOffer;
    }

    public void setIdRunOffer(String idRunOffer) {
        this.idRunOffer = idRunOffer;
    }
}
