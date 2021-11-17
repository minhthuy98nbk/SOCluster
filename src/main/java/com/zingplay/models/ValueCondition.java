package com.zingplay.models;

import java.util.HashMap;

/**
 * Created by thuydtm on 9/25/2021.
 */
public class ValueCondition {
    private HashMap<String, String> mapStrParams;

    public ValueCondition() {
    }

    public ValueCondition(ValueCondition other) {
        if (other.getMapStrParams() != null) {
            mapStrParams = new HashMap<>();
        }
    }

    public ValueCondition(HashMap<String, String> inListStr) {
        this.mapStrParams = inListStr;
    }

    public void setParamTracking(String key, String value){
        if(mapStrParams == null) mapStrParams = new HashMap<>();
        mapStrParams.put(key,value);
    }

    public boolean isValid () {
        return mapStrParams != null && mapStrParams.size() > 0;
    }

    public boolean equals(ValueCondition o) {
        if (this == o) return true;
        if (mapStrParams != null && mapStrParams.size() > 0) {
            for (String s : mapStrParams.keySet()) {
                if (!o.getMapStrParams().get(s).equals(mapStrParams.get(s))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "ValueCondition" + ((mapStrParams != null) ? "stringParams=" + mapStrParams : "null");
    }

    // region get set

    public HashMap<String, String> getMapStrParams() {
        return mapStrParams;
    }

    public void setMapStrParams(HashMap<String, String> mapStrParams) {
        this.mapStrParams = mapStrParams;
    }

    // endregion get set
}
