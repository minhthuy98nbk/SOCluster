package com.zingplay.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "user_received")
public class UserReceived {
    @Id
    private String id;

    @Indexed
    @DBRef
    private User user;

    @Indexed
    @DBRef
    private RunOffer runOffer;

    @CreatedDate
    private Date timeCreate;
    @LastModifiedDate
    private Date timeUpdate;

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
}
