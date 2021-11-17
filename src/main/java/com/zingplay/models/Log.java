package com.zingplay.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "logs")
public class Log {
    @Id
    private String id;
    private String username;
    private String game;
    private String country;
    private int level;
    private String message;
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

    public String getCountry() {
        return country;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", level='" + level + '\'' +
                ", message='" + message + '\'' +
                ", timeCreate=" + timeCreate +
                '}';
    }
}
