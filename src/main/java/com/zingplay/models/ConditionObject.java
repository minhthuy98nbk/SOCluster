package com.zingplay.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Document(collection = "condition_obj")
public class ConditionObject {
    @Id
    private String id;

    private HashMap<String, List<ValueCondition>> inListObject;
    private HashMap<String, List<String>> inListStr;//gia trị thuộc list
    private HashMap<String, List<Long>> inListLong;
    private HashMap<String, List<Float>> inListFloat;

    private HashMap<String, List<Long>> inRangeLong;//gia trị thuộc khoảng
    private HashMap<String, List<Float>> inRangeFloat;

    private HashMap<String, List<Long>> inRangeDuration; //gia trị khoảng thời gian tính từ hiện tại
    private HashMap<String, List<Long>> inListDuration;

    @CreatedDate
    private Date timeCreate;

    // region get set

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public HashMap<String, List<ValueCondition>> getInListObject() {
        return inListObject;
    }

    public void setInListObject(HashMap<String, List<ValueCondition>> inListObject) {
        this.inListObject = inListObject;
    }

    public HashMap<String, List<String>> getInListStr() {
        return inListStr;
    }

    public void setInListStr(HashMap<String, List<String>> inListStr) {
        this.inListStr = inListStr;
    }

    public HashMap<String, List<Long>> getInListLong() {
        return inListLong;
    }

    public void setInListLong(HashMap<String, List<Long>> inListLong) {
        this.inListLong = inListLong;
    }

    public HashMap<String, List<Float>> getInListFloat() {
        return inListFloat;
    }

    public void setInListFloat(HashMap<String, List<Float>> inListFloat) {
        this.inListFloat = inListFloat;
    }

    public HashMap<String, List<Long>> getInRangeLong() {
        return inRangeLong;
    }

    public void setInRangeLong(HashMap<String, List<Long>> inRangeLong) {
        this.inRangeLong = inRangeLong;
    }

    public HashMap<String, List<Float>> getInRangeFloat() {
        return inRangeFloat;
    }

    public void setInRangeFloat(HashMap<String, List<Float>> inRangeFloat) {
        this.inRangeFloat = inRangeFloat;
    }

    public HashMap<String, List<Long>> getInRangeDuration() {
        return inRangeDuration;
    }

    public void setInRangeDuration(HashMap<String, List<Long>> inRangeDuration) {
        this.inRangeDuration = inRangeDuration;
    }

    public HashMap<String, List<Long>> getInListDuration() {
        return inListDuration;
    }

    public void setInListDuration(HashMap<String, List<Long>> inListDuration) {
        this.inListDuration = inListDuration;
    }

    // endregion get set

    @Override
    public String toString() {
        return "ConditionObject{" +
                "id='" + id + '\'' +
                ", inListObject=" + inListObject +
                ", inListStr=" + inListStr +
                ", inListLong=" + inListLong +
                ", inListFloat=" + inListFloat +
                ", inRangeLong=" + inRangeLong +
                ", inRangeFloat=" + inRangeFloat +
                ", inRangeDuration=" + inRangeDuration +
                ", inListDuration=" + inListDuration +
                ", timeCreate=" + timeCreate +
                '}';
    }
}
