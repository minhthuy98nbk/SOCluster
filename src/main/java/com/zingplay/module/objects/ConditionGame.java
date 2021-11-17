package com.zingplay.module.objects;

import com.zingplay.helpers.TrackingHelpers;
import com.zingplay.models.Condition;
import com.zingplay.models.ValueCondition;

import java.util.HashMap;
import java.util.List;

public class ConditionGame {
    private HashMap<String, ConditionRuntime> conditions;

    public HashMap<String, ConditionRuntime> getConditions() {
        return conditions;
    }

    public void addOrUpdateCondition(Condition condition){
        if(conditions == null){
            conditions = new HashMap<>();
        }
        ConditionRuntime conditionRuntime = new ConditionRuntime();
        conditionRuntime.setId(condition.getId());
        conditionRuntime.setKey(condition.getKey());
        conditionRuntime.setType(condition.getType());
        List<String> samples = condition.getSamples();
        try {
            //TrackingHelpers.convertToLongSamples(condition.getSamples());
            switch (condition.getType()){
                case ConditionConfig.STRING:
                    conditionRuntime.setSamplesStr(samples);
                    break;
                case ConditionConfig.FLOAT:
                    conditionRuntime.setSamplesFloat(TrackingHelpers.convertToFloatSamples(condition.getSamples()));
                    break;
                case ConditionConfig.LONG:
                    conditionRuntime.setSamplesLong(TrackingHelpers.convertToLongSamples(condition.getSamples()));
                    break;
                case ConditionConfig.DURATION:
                    conditionRuntime.setSampleDuration(TrackingHelpers.convertToLongSamples(condition.getSamples()));
                    break;
                case ConditionConfig.OBJECT:
                    conditionRuntime.setSamplesObject(TrackingHelpers.convertToObjectSample(condition.getSubConditions(), condition.getSubSamples()));
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException e){
            return;
        }
        conditions.put(condition.getKey(),conditionRuntime);
    }
    public void removeCondition(String key){
        if(conditions != null){
            conditions.remove(key);
        }
    }
    public void setConditions(HashMap<String, ConditionRuntime> conditions) {
        this.conditions = conditions;
    }

    public <T> boolean isValidAll(String key,T value, String conditionType){
        try {
            ConditionRuntime condition = conditions.get(key);
            if(condition != null){
                if(conditionType.equals(condition.getType())){
                    List<?> samples = getSamples(condition, conditionType);
                    if(samples != null && samples.size() > 0){
                        if (conditionType.equals(ConditionConfig.OBJECT)) {
                            return listObjectContain(samples, value);
                        }
                        return samples.contains(value);
                    }
                    return checkType(value + "", conditionType);
                }
            }
            else {
                // gửi dư thì k lưu
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private <K, V>boolean listObjectContain(List<K> list, V value) {
        if (value instanceof ValueCondition) {
            ValueCondition userValue = (ValueCondition) value;
            for (Object obj : list) {
                if (obj instanceof ValueCondition) {
                    ValueCondition valueCondition = (ValueCondition) obj;
                    if (valueCondition.equals(userValue)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<?> getSamples(ConditionRuntime condition, String conditionType){
        switch (conditionType){
            case ConditionConfig.FLOAT:
                return condition.getSamplesFloat();
            case ConditionConfig.LONG:
                return condition.getSamplesLong();
            case ConditionConfig.OBJECT:
                return condition.getSamplesObject();
            case ConditionConfig.DURATION:
                return condition.getSampleDuration();
            default:
                return condition.getSamplesStr();
        }
    }
    boolean checkType(String value, String conditionType){
        try {
            switch (conditionType){
                case ConditionConfig.FLOAT:
                    Float.parseFloat(value);
                    break;
                case ConditionConfig.LONG:
                case ConditionConfig.DURATION:
                    Long.parseLong(value);
                    break;
                default:
                    return !value.trim().equals("");
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
