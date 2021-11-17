package com.zingplay.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "condition")
public class Condition {
    @Id
    private String id;
    @Indexed
    private String key;//key tracking
    private String name;//ten hien thi
    private String type;//kiểu dữ liệu: string|long|float|object
    // if type = object
    private List<SubCondition> subConditions;
    private List<List<String>> subSamples;
    // else
    private List<String> samples; // các giá trị có thể chọn
    private boolean canEdit;


    @CreatedDate
    private Date timeCreate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

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

    public List<SubCondition> getSubConditions() {
        return subConditions;
    }

    public void setSubConditions(List<SubCondition> subConditions) {
        this.subConditions = subConditions;
    }

    public List<List<String>> getSubSamples() {
        return subSamples;
    }

    public void setSubSamples(List<List<String>> subSamples) {
        this.subSamples = subSamples;
    }

    public List<String> getSamples() {
        return samples;
    }

    public void setSamples(List<String> samples) {
        this.samples = samples;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", canEdit='" + canEdit + '\'' +
                (samples != null && samples.size() > 0 ? (", samples=" + samples) : "" )+
                (subConditions != null && subConditions.size() > 0 ? (", subConditions=" + subConditions) : "" )+
                (subSamples != null && subSamples.size() > 0 ? (", subSamples=" + subSamples) : "" )+
                ", timeCreate=" + timeCreate +
                '}';
    }
}
