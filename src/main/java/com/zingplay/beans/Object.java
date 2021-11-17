package com.zingplay.beans;

import java.io.Serializable;
import java.util.Set;

public class Object implements Serializable {
    private boolean override;
    private String idObject;

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

    private boolean scanNow;
    private Set<TimeSchedule> times;
    private Set<Long> timeStart;

    public Set<Long> getTimeStart() {
        return timeStart;
    }

    public boolean isOverride(){
        return override;
    }
    public String getIdObject() {
        return idObject;
    }

    public void setIdObject(String idObject) {
        this.idObject = idObject;
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

    public boolean isScanNow() {
        return scanNow;
    }

    public void setScanNow(boolean scanNow) {
        this.scanNow = scanNow;
    }

    public Set<TimeSchedule> getTimes() {
        return times;
    }

    public void setTimes(Set<TimeSchedule> times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "Object{" +
                "override=" + override +
                ", idObject='" + idObject + '\'' +
                ", game='" + game + '\'' +
                ", country='" + country + '\'' +
                ", nameObject='" + nameObject + '\'' +
                ", note='" + note + '\'' +
                ", channelPayments=" + channelPayments +
                ", lastPaidAmounts=" + lastPaidAmounts +
                ", paidTotalMin=" + paidTotalMin +
                ", paidTotalMax=" + paidTotalMax +
                ", paidTimesMin=" + paidTimesMin +
                ", paidTimesMax=" + paidTimesMax +
                ", totalGameMin=" + totalGameMin +
                ", totalGameMax=" + totalGameMax +
                ", ageUserMin=" + ageUserMin +
                ", ageUserMax=" + ageUserMax +
                ", timeOnlineMin=" + timeOnlineMin +
                ", timeOnlineMax=" + timeOnlineMax +
                ", channelMin=" + channelMin +
                ", channelMax=" + channelMax +
                ", scanNow=" + scanNow +
                ", times=" + times +
                ", timeStart=" + timeStart +
                '}';
    }

    public void autoTrim() {
        if(idObject != null){
            idObject = idObject.trim();
        }
    }
}
