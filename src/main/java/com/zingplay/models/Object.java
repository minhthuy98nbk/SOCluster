package com.zingplay.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Document(collection = "object")
public class Object {
    @Id
    private String id;
    private String idObject;
    private int status;
    private long totalUser;
    @CreatedDate
    private Date timeCreate;
    @LastModifiedDate
    private Date timeUpdate;
    @DBRef
    private ConditionObject condition;

    private int scheduleFrequency;

    @Override
    public String toString() {
        return "Object{" +
                "id='" + id + '\'' +
                ", idObject='" + idObject + '\'' +
                ", status=" + status +
                ", totalUser=" + totalUser +
                ", timeCreate=" + timeCreate +
                ", timeUpdate=" + timeUpdate +
                ", condition=" + condition +
                ", game='" + game + '\'' +
                ", country='" + country + '\'' +
                ", nameObject='" + nameObject + '\'' +
                '}';
    }

    //xoa het dong phia duoi khong dung
    private String game;
    private String country;
    private String nameObject;
    private String note;

    private Set<String> channelPayments;
    private Set<String> lastPaidAmounts;

    private int paidTotalMin;
    private int paidTotalMax;

    private int paidTimesMin;
    private int paidTimesMax;

    private int totalGameMin;
    private int totalGameMax;

    private int ageUserMin;
    private int ageUserMax;

    private int timeOnlineMin;
    private int timeOnlineMax;

    private int channelMin;
    private int channelMax;


    public ConditionObject getCondition() {
        return condition;
    }

    public void setCondition(ConditionObject condition) {
        this.condition = condition;
    }

    public long getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(long totalUser) {
        this.totalUser = totalUser;
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

    public String getIdObject() {
        return idObject;
    }

    public void setIdObject(String idObject) {
        this.idObject = idObject;
    }

    public String getNameObject() {
        return nameObject;
    }

    public void setNameObject(String nameObject) {
        this.nameObject = nameObject;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<String> getChannelPayments() {
        return channelPayments;
    }

    public void setChannelPayments(Set<String> channelPayments) {
        this.channelPayments = channelPayments;
    }

    public Set<String> getLastPaidAmounts() {
        return lastPaidAmounts;
    }

    public void setLastPaidAmounts(Set<String> lastPaidAmounts) {
        this.lastPaidAmounts = lastPaidAmounts;
    }

    public int getPaidTotalMin() {
        return paidTotalMin;
    }

    public void setPaidTotalMin(int paidTotalMin) {
        this.paidTotalMin = paidTotalMin;
    }

    public int getPaidTotalMax() {
        return paidTotalMax;
    }

    public void setPaidTotalMax(int paidTotalMax) {
        this.paidTotalMax = paidTotalMax;
    }

    public int getPaidTimesMin() {
        return paidTimesMin;
    }

    public void setPaidTimesMin(int paidTimesMin) {
        this.paidTimesMin = paidTimesMin;
    }

    public int getPaidTimesMax() {
        return paidTimesMax;
    }

    public void setPaidTimesMax(int paidTimesMax) {
        this.paidTimesMax = paidTimesMax;
    }

    public int getTotalGameMin() {
        return totalGameMin;
    }

    public void setTotalGameMin(int totalGameMin) {
        this.totalGameMin = totalGameMin;
    }

    public int getTotalGameMax() {
        return totalGameMax;
    }

    public void setTotalGameMax(int totalGameMax) {
        this.totalGameMax = totalGameMax;
    }

    public int getAgeUserMin() {
        return ageUserMin;
    }

    public void setAgeUserMin(int ageUserMin) {
        this.ageUserMin = ageUserMin;
    }

    public int getAgeUserMax() {
        return ageUserMax;
    }

    public void setAgeUserMax(int ageUserMax) {
        this.ageUserMax = ageUserMax;
    }

    public int getTimeOnlineMin() {
        return timeOnlineMin;
    }

    public void setTimeOnlineMin(int timeOnlineMin) {
        this.timeOnlineMin = timeOnlineMin;
    }

    public int getTimeOnlineMax() {
        return timeOnlineMax;
    }

    public void setTimeOnlineMax(int timeOnlineMax) {
        this.timeOnlineMax = timeOnlineMax;
    }

    public int getChannelMin() {
        return channelMin;
    }

    public void setChannelMin(int channelMin) {
        this.channelMin = channelMin;
    }

    public int getChannelMax() {
        return channelMax;
    }

    public void setChannelMax(int channelMax) {
        this.channelMax = channelMax;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getScheduleFrequency() {
        return scheduleFrequency;
    }

    public void setScheduleFrequency(int scheduleFrequency) {
        this.scheduleFrequency = scheduleFrequency;
    }
}
