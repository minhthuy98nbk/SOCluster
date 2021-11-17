package com.zingplay.models;

import java.io.Serializable;

public class Item implements Serializable {

    private String id;
    private long value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
