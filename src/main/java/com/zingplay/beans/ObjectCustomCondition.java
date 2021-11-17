package com.zingplay.beans;

import com.zingplay.models.ConditionObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ObjectCustomCondition implements Serializable {
    private String idObject;

    private ConditionObject condition;

    private Set<TimeSchedule> times;

    private int scheduleFrequency;

    public String getIdObject() {
        return idObject;
    }

    public ConditionObject getCondition() {
        return condition;
    }

    public void setCondition(ConditionObject condition) {
        this.condition = condition;
    }

    public Set<TimeSchedule> getTimes() {
        return times;
    }

    public void setTimes(Set<TimeSchedule> times) {
        this.times = times;
    }

    public void setIdObject(String idObject) {
        this.idObject = idObject;
    }

    public void autoTrim() {
        if(idObject != null){
            idObject = idObject.trim();
        }
    }

    public int getScheduleFrequency() {
        return scheduleFrequency;
    }

    public void setScheduleFrequency(int scheduleFrequency) {
        this.scheduleFrequency = scheduleFrequency;
    }

    @Override
    public String toString() {
        return "ObjectCustomCondition{" +
                "idObject='" + idObject + '\'' +
                ", condition=" + condition +
                ", times=" + times +
                ", scheduleFrequency= "+scheduleFrequency+
                '}';
    }
}
