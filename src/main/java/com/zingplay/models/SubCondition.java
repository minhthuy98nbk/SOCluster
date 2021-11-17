package com.zingplay.models;

public class SubCondition {
    private String key;//key tracking
    private String name;//ten hien thi
    private String type;//kiểu dữ liệu: string|long|float|object

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Condition{" +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
