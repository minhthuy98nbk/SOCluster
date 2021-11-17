package com.zingplay.socket.v3.response;

import com.zingplay.models.Item;

import java.util.Set;

public class DataCustom {
    String typeCustom;
    String dataId;
    String dataName;
    private long timeStart;
    private long timeEnd;
    Set<Item> items;

    public void setTypeCustom(String typeCustom) {
        this.typeCustom = typeCustom;
    }

    public String getTypeCustom() {
        return typeCustom;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    @Override
    public String toString() {
        return "DataCustom{" +
                "typeCustom=" + typeCustom +
                ", dataId='" + dataId + '\'' +
                ", dataName='" + dataName + '\'' +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", items=" + items +
                '}';
    }
}
