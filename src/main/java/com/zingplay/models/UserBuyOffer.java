package com.zingplay.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "user_buy_offer")
public class UserBuyOffer {
    @Id
    private String id;

    @Indexed
    @DBRef
    private User user;

    @DBRef
    private RunOffer runOffer;

    private float amount;

     @CreatedDate
    private Date timeCreate;

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RunOffer getRunOffer() {
        return runOffer;
    }

    public void setRunOffer(RunOffer runOffer) {
        this.runOffer = runOffer;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
