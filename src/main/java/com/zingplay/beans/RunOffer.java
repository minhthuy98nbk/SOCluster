package com.zingplay.beans;

import java.io.Serializable;
import java.util.Date;

public class RunOffer implements Serializable {
    private boolean override;
    private String idRunOffer;
    private String idObject;
    private String idOffer;

    private int priority;//cang lon cang uu tien
    private Date timeStart;
    private Date timeEnd;

    private String note;

    private Date timeCreate;
    private Date timeUpdate;

    private String game;
    private String country;

    private int scheduleFrequency;
    private Date timeEndSchedule;
    private int preTimeSetSchedule;

    public boolean isOverride(){
        return override;
    }

    public String getIdObject() {
        return idObject;
    }

    public void setIdObject(String idObject) {
        this.idObject = idObject;
    }

    public String getIdOffer() {
        return idOffer;
    }

    public void setIdOffer(String idOffer) {
        this.idOffer = idOffer;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getIdRunOffer() {
        return idRunOffer;
    }

    public void setIdRunOffer(String idRunOffer) {
        this.idRunOffer = idRunOffer;
    }

    public int getScheduleFrequency() {
        return scheduleFrequency;
    }

    public void setScheduleFrequency(int scheduleFrequency) {
        this.scheduleFrequency = scheduleFrequency;
    }

    public Date getTimeEndSchedule() {
        return timeEndSchedule;
    }

    public void setTimeEndSchedule(Date timeEndSchedule) {
        this.timeEndSchedule = timeEndSchedule;
    }

    public int getPreTimeSetSchedule() {
        return preTimeSetSchedule;
    }

    public void setPreTimeSetSchedule(int preTimeSetSchedule) {
        this.preTimeSetSchedule = preTimeSetSchedule;
    }

    @Override
    public String toString() {
        return "RunOffer{" +
                "override=" + override +
                ", idRunOffer='" + idRunOffer + '\'' +
                ", idObject='" + idObject + '\'' +
                ", idOffer='" + idOffer + '\'' +
                ", priority=" + priority +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", note='" + note + '\'' +
                ", timeCreate=" + timeCreate +
                ", timeUpdate=" + timeUpdate +
                ", game='" + game + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
    public void autoTrim(){
        if(idRunOffer != null){
            idRunOffer = idRunOffer.trim();
        }
        if(idObject != null){
            idObject = idObject.trim();
        }
        if(idOffer != null){
            idOffer = idOffer.trim();
        }
    }
}
