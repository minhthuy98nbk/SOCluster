package com.zingplay.module.objects;

import com.zingplay.models.ValueCondition;

import java.util.List;

public class ConditionRuntime {
    private String id;
    private String key;//key tracking
    private String type;//kiểu dữ liệu string|int|long|float
    private List<String> samplesStr;//những giá trị có thể chọn
    private List<Long> samplesLong;//những giá trị có thể chọn
    private List<Float> samplesFloat;//những giá trị có thể chọn
    private List<ValueCondition> samplesObject;//những giá trị có thể chọn
    private List<Long> sampleDuration;//những giá trị có thể chọn

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getSamplesStr() {
        return samplesStr;
    }

    public void setSamplesStr(List<String> samplesStr) {
        this.samplesStr = samplesStr;
    }

    public List<Long> getSamplesLong() {
        return samplesLong;
    }

    public void setSamplesLong(List<Long> samplesLong) {
        this.samplesLong = samplesLong;
    }

    public List<Float> getSamplesFloat() {
        return samplesFloat;
    }

    public void setSamplesFloat(List<Float> samplesFloat) {
        this.samplesFloat = samplesFloat;
    }

    public List<ValueCondition> getSamplesObject() {
        return samplesObject;
    }

    public void setSamplesObject(List<ValueCondition> samplesObject) {
        this.samplesObject = samplesObject;
    }

    public List<Long> getSampleDuration() {
        return sampleDuration;
    }

    public void setSampleDuration(List<Long> sampleDuration) {
        this.sampleDuration = sampleDuration;
    }
}
