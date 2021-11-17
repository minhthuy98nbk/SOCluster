package com.zingplay.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "run_offer")
public class  RunOffer {
    @Id
    private String id;

    private String idRunOffer;

    @Indexed
    @DBRef
    private Object object;
    @DBRef
    private Offer offer;

    private int priority;//cang lon cang uu tien

    private Date timeStart;

    @Indexed
    private Date timeEnd;

    private String note;

    private int status;//trang thai da quet chua quet -> khi cap nhat (-1 chua 0 - dang quet | 1= da quet
    @CreatedDate
    private Date timeCreate;
    @LastModifiedDate
    private Date timeUpdate;

    private String game;
    private String country;

    private long countTotal;//tong so luong user có thể nhận
    private long countReceived;//so luong user đa nhận được offer

    private long countUser;//so luong user mua offer
    private float totalRev;//tong so doanh thu

    private int scheduleFrequency;
    private Date timeEndSchedule;
    private int preTimeSetSchedule;


    @Override
    public int hashCode() {
        return idRunOffer.hashCode();
    }


    public long getCountTotal() {
        return countTotal;
    }

    public void setCountTotal(long countTotal) {
        this.countTotal = countTotal;
    }

    public long getCountReceived() {
        return countReceived;
    }

    public void setCountReceived(long countReceived) {
        this.countReceived = countReceived;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
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

    @Override
    public boolean equals(java.lang.Object obj) {
        if(obj instanceof RunOffer){
            return idRunOffer.equals(((RunOffer) obj).getIdRunOffer());
            //return this.getKey().equals(((RunOffer) obj).getKey());
        }
        return super.equals(obj);
    }
    @JsonIgnore
    private String getKey(){
        String idObject;
        if(object == null){
            idObject = "";
        }else{
            idObject = object.getIdObject();
        }
        String idOffer;
        if(offer == null){
            idOffer = "";
        }else{
            idOffer = offer.getIdOffer();
        }
        return this.idRunOffer + idObject + idOffer;
    }

    public long getCountUser() {
        return countUser;
    }

    public void setCountUser(long countUser) {
        this.countUser = countUser;
    }

    public float getTotalRev() {
        return totalRev;
    }

    public void setTotalRev(float totalRev) {
        this.totalRev = totalRev;
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
}
