package com.zingplay.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "custom_gift")
public class CustomGift {
    @Id
    private String id;
    private String giftName;
    private Date timeStart;
    private Date timeEnd;
    private String objectId;
    private List<Item> listItem;
    private long numReceive = 0;
    private long totalUser = 0;

    @CreatedDate
    private Date timeCreate;
    @LastModifiedDate
    private Date timeUpdate;

    public void autoTrim() {
        if(giftName != null){
            giftName = giftName.trim();
        }
    }

    @Override
    public String toString() {
        return "CustomGift{" +
                "id='" + id + '\'' +
                ", giftName='" + giftName + '\'' +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", listObject=" + objectId +
                ", listItem=" + listItem +
                ", timeCreate=" + timeCreate +
                ", timeUpdate=" + timeUpdate +
                ", numReceive=" + numReceive +
                ", totalUser=" + totalUser +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public List<Item> getListItem() {
        return listItem;
    }

    public void setListItem(List<Item> listItem) {
        this.listItem = listItem;
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

    public long getNumReceive() {
        return numReceive;
    }

    public void setNumReceive(long numReceive) {
        this.numReceive = numReceive;
    }

    public long getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(long totalUser) {
        this.totalUser = totalUser;
    }
}
