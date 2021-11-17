package com.zingplay.beans;

import java.io.Serializable;
import java.util.Date;

public class CronProcess implements Serializable {
    private long id;
    private int groupId;
    private String game;
    private String country;
    private String idProcess;
    private String name;
    private long timeStart;
    private int status;
    private long processRecord;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdProcess() {
        return idProcess;
    }

    public void setIdProcess(String idProcess) {
        this.idProcess = idProcess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getProcessRecord() {
        return processRecord;
    }

    public void setProcessRecord(long processRecord) {
        this.processRecord = processRecord;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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
}
