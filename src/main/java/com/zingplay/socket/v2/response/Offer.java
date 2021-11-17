package com.zingplay.socket.v2.response;

import com.google.gson.annotations.SerializedName;
import com.zingplay.models.Item;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Offer implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("n")
    private String name;
    @SerializedName("p")
    private int priority;

    @SerializedName("$")
    private Price price;

    @SerializedName("i")
    private int iconNum;
    @SerializedName("t")
    private int themeNum;

    @SerializedName("s")
    private long timeStart;
    @SerializedName("e")
    private long timeEnd;

    @SerializedName("l")
    private Set<Item> items;

    public Price getPrice() {
        return price;
    }

    public int getThemeNum() {
        return themeNum;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
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

    public Set<Item> getItems() {
        return items;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public void setIconNum(int iconNum) {
        this.iconNum = iconNum;
    }

    public void setThemeNum(int themeNum) {
        this.themeNum = themeNum;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", price=" + price +
                ", iconNum=" + iconNum +
                ", themeNum=" + themeNum +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", items=" + items +
                '}';
    }
}
