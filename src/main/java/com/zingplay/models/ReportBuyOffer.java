package com.zingplay.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "report_buy_offer")
public class ReportBuyOffer {
    @Id
    private String id;

    @Indexed
    private String game;
    @Indexed
    private String country;
    @Indexed
    private String date;

    private long totalUser;
    private float totalRev;


    @CreatedDate
    private Date timeCreate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public long getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(long totalUser) {
        this.totalUser = totalUser;
    }

    public float getTotalRev() {
        return totalRev;
    }

    public void setTotalRev(float totalRev) {
        this.totalRev = totalRev;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }
}
