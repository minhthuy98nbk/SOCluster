package com.zingplay.beans;

import java.io.Serializable;
import java.util.Date;

public class TimeSchedule implements Serializable {
    private String name;
    private Date timeScan;

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
}
