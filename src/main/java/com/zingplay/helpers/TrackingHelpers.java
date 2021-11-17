package com.zingplay.helpers;

import com.zingplay.models.SubCondition;
import com.zingplay.models.ValueCondition;
import com.zingplay.module.objects.ConditionConfig;
import com.zingplay.socket.v3.TrackingCommon;
import com.zingplay.socket.v3.UserTrackingCustom;

import java.util.*;

public class TrackingHelpers {
    public static long getTimeCurrent(UserTrackingCustom tracking) {
        if(tracking != null){
            return tracking.getTrackingLong(TrackingCommon.TimeCurrent);
        }
        return 0;
    }
    public static List<Float> convertToFloatSamples(List<String> samples) {
        if(samples == null){
            return null;
        }
        List<Float> result = new ArrayList<>();
        for (String sample : samples) {
            result.add(Float.parseFloat(sample));
        }
        return result;
    }

    public static List<Long> convertToLongSamples(List<String> samples) {
        if(samples == null){
            return null;
        }
        List<Long> result = new ArrayList<>();
        for (String sample : samples) {
            result.add(Long.parseLong(sample));
        }
        return result;
    }

    public static List<ValueCondition> convertToObjectSample(List<SubCondition> subConditionList, List<List<String>> subSamples) {
        if(subConditionList == null){
            return null;
        }
        List<ValueCondition> result = new ArrayList<>();
        int countParam = subConditionList.size();

        // init
        for(List<String> sample : subSamples) {
            if (sample.size() != countParam) {
                return null;
            }
            ValueCondition temp = new ValueCondition();
            for (int i = 0; i < subConditionList.size(); i++){
                SubCondition subCondition = subConditionList.get(i);
                switch (subCondition.getType()) {
                    case ConditionConfig.STRING:
                        if (temp.getMapStrParams() == null) {
                            temp.setMapStrParams(new HashMap<>());
                        }
                        temp.getMapStrParams().put(subCondition.getKey(), sample.get(i));
                        break;
                    case ConditionConfig.FLOAT:
                        if (temp.getMapFloatParams() == null) {
                            temp.setMapFloatParams(new HashMap<>());
                        }
                        temp.getMapFloatParams().put(subCondition.getKey(), Float.parseFloat(sample.get(i)));
                        break;
                    case ConditionConfig.LONG:
                        if (temp.getMapLongParams() == null) {
                            temp.setMapLongParams(new HashMap<>());
                        }
                        temp.getMapLongParams().put(subCondition.getKey(), Long.parseLong(sample.get(i)));
                        break;
                }
            }
            result.add(temp);
        }
        return result;
    }

}
