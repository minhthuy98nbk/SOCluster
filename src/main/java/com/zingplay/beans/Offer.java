package com.zingplay.beans;

import com.zingplay.models.Item;
import com.zingplay.models.Price;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Offer implements Serializable {
    private boolean override;

    private String idOffer;
    private String nameOffer;
    private String displayName;
    private String note;

    private String region;
    private int basePrice;
    private float price;
    private int bonus;
    private int iconNum;
    private int themeNum;

    private Set<Item> items;

    private Date timeCreate;
    private Date timeUpdate;

    private String game;
    private String country;

    //v2 custom price
    private String currency;
    private Set<String> channels;
    private Set<Price> prices;


    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setChannels(Set<String> channels) {
        this.channels = channels;
    }

    public void setPrices(Set<Price> prices) {
        this.prices = prices;
    }

    public String getCurrency() {
        return currency;
    }

    public Set<String> getChannels() {
        return channels;
    }

    public Set<Price> getPrices() {
        return prices;
    }

    public void setThemeNum(int themeNum) {
        this.themeNum = themeNum;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isOverride(){
        return override;
    }


    public String getIdOffer() {
        return idOffer;
    }

    public void setIdOffer(String idOffer) {
        this.idOffer = idOffer;
    }

    public String getNameOffer() {
        return nameOffer;
    }

    public void setNameOffer(String nameOffer) {
        this.nameOffer = nameOffer;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
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

    public int getIconNum() {
        return iconNum;
    }

    public int getThemeNum() {
        return themeNum;
    }

    public void setIconNum(int iconNum) {
        this.iconNum = iconNum;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public Date getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(Date timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

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

    @Override
    public String toString() {
        return "Offer{" +
                "override=" + override +
                ", idOffer='" + idOffer + '\'' +
                ", nameOffer='" + nameOffer + '\'' +
                ", displayName='" + displayName + '\'' +
                ", note='" + note + '\'' +
                ", basePrice=" + basePrice +
                ", price=" + price +
                ", bonus=" + bonus +
                ", iconNum=" + iconNum +
                ", items=" + items +
                ", timeCreate=" + timeCreate +
                ", timeUpdate=" + timeUpdate +
                ", game='" + game + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public void autoTrim() {
        if(idOffer != null){
            idOffer = idOffer.trim();
        }
    }
}
