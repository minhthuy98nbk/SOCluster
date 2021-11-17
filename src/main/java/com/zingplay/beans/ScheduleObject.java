package com.zingplay.beans;

import java.io.Serializable;
import java.util.Date;

public class ScheduleObject implements Serializable {
    private String idObject;

    private String name;

    private Date timeScan;

    public String getIdObject() {
        return idObject;
    }

    public void setIdObject(String idObject) {
        this.idObject = idObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimeScan() {
        return timeScan;
    }

    public void setTimeScan(Date timeScan) {
        this.timeScan = timeScan;
    }

    @Override
    public String toString() {
        return "ScheduleObject{" +
                "idObject='" + idObject + '\'' +
                ", name='" + name + '\'' +
                ", timeScan=" + timeScan +
                '}';
    }
}
