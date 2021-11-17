package com.zingplay.module.objects;

import com.zingplay.models.Condition;
import com.zingplay.models.ConditionObject;
import com.zingplay.models.User;
import com.zingplay.models.ValueCondition;
import com.zingplay.socket.v3.TrackingCommon;
import com.zingplay.socket.v3.TypeParam;
import com.zingplay.socket.v3.TypeUpdateParam;
import com.zingplay.socket.v3.UserTrackingCustom;

import java.util.HashMap;
import java.util.List;

public class ConditionController {
    private static final ConditionController instance = new ConditionController();
    public static ConditionController getInstance(){ return instance; }

    HashMap<String,ConditionGame> conditionGameHashMap;

    public ConditionController(){
        this.conditionGameHashMap = new HashMap<>();
    }

    public void addOrUpdateCondition(String game, Condition condition){
        ConditionGame conditionGame = getCondition(game);
        if(conditionGame == null){
            conditionGame = new ConditionGame();
            conditionGameHashMap.put(game,conditionGame);
        }
        conditionGame.addOrUpdateCondition(condition);
    }
    public void removeCondition(String game, Condition condition){
        ConditionGame conditionGame = getCondition(game);
        if(conditionGame!=null){
            conditionGame.removeCondition(condition.getKey());
        }
    }

    public ConditionGame getCondition(String game){
        ConditionGame conditionGame = conditionGameHashMap.get(game);
        return conditionGame;
    }

    public boolean isValidAllKey(String game, UserTrackingCustom tracking) {
        ConditionGame condition = getCondition(game);
        HashMap<String, String> trackingStr = tracking.getTrackingStr();
        HashMap<String, Long> trackingLong = tracking.getTrackingLong();
        HashMap<String, Float> trackingFloat = tracking.getTrackingFloat();
        HashMap<String, ValueCondition> trackingObject = tracking.getTrackingObject();
        HashMap<String, Long> trackingDuration = tracking.getTrackingDuration();
        boolean allMatch = true;
        if(trackingStr != null){
            allMatch = trackingStr.entrySet().stream().allMatch(stringStringEntry -> condition.isValidAll(stringStringEntry.getKey(),stringStringEntry.getValue(), ConditionConfig.STRING));
        }
        if(trackingLong != null && allMatch){
            allMatch = trackingLong.entrySet().stream().allMatch(stringStringEntry -> condition.isValidAll(stringStringEntry.getKey(),stringStringEntry.getValue(), ConditionConfig.LONG));
        }
        if(trackingFloat != null && allMatch){
            allMatch = trackingFloat.entrySet().stream().allMatch(stringStringEntry -> condition.isValidAll(stringStringEntry.getKey(),stringStringEntry.getValue(), ConditionConfig.FLOAT));
        }
        if(trackingObject != null && allMatch){
            allMatch = trackingObject.entrySet().stream().allMatch(stringStringEntry -> condition.isValidAll(stringStringEntry.getKey(),stringStringEntry.getValue(), ConditionConfig.OBJECT));
        }
        if(trackingDuration != null && allMatch){
            allMatch = trackingDuration.entrySet().stream().allMatch(stringStringEntry -> condition.isValidAll(stringStringEntry.getKey(),stringStringEntry.getValue(), ConditionConfig.DURATION));
        }
        return allMatch;
    }

    public void updateData(String game, User user, UserTrackingCustom tracking) {
        if(isValidAllKey(game,tracking)){
            HashMap<String, String> trackingStr = tracking.getTrackingStr();
            HashMap<String, Long> trackingLong = tracking.getTrackingLong();
            HashMap<String, Float> trackingFloat = tracking.getTrackingFloat();
            HashMap<String, ValueCondition> trackingObject = tracking.getTrackingObject();
            HashMap<String, Long> trackingDuration = tracking.getTrackingDuration();
            if(trackingStr != null){
                trackingStr.forEach(user::setTracking);
            }
            if(trackingLong != null){
                trackingLong.forEach((key, value) -> user.setTracking(key, value, TypeParam.LONG, TypeUpdateParam.SET));
            }
            if(trackingFloat != null){
                trackingFloat.forEach((key, value) -> user.setTracking(key, value, TypeUpdateParam.SET));
            }
            if(trackingObject != null){
                trackingObject.forEach(user::setTracking);
            }
            if(trackingDuration != null){
                trackingDuration.forEach((key, value) -> user.setTracking(key, value, TypeParam.DURATION, TypeUpdateParam.SET));
            }
        }
    }

    public boolean isValidAllKey(String game, ConditionObject conditionObject) {
        ConditionGame condition = getCondition(game);
        HashMap<String, List<ValueCondition>> inListObject = conditionObject.getInListObject();
        HashMap<String, List<Float>> inListFloat = conditionObject.getInListFloat();
        HashMap<String, List<Long>> inListLong = conditionObject.getInListLong();
        HashMap<String, List<String>> inListStr = conditionObject.getInListStr();
        HashMap<String, List<Long>> inRangeLong = conditionObject.getInRangeLong();
        HashMap<String, List<Long>> inRangeDuration = conditionObject.getInRangeDuration();
        HashMap<String, List<Long>> inListDuration = conditionObject.getInListDuration();
        HashMap<String, List<Float>> inRangeFloat = conditionObject.getInRangeFloat();

        boolean allMatch = true;
        if(inListFloat != null && inListFloat.size() > 0){
            allMatch = checkAllMatch(inListFloat, condition, ConditionConfig.FLOAT);
        }
        if(allMatch && inListLong != null && inListLong.size() > 0){
            allMatch = checkAllMatch(inListLong, condition, ConditionConfig.LONG);
        }
        if(allMatch && inListStr != null && inListStr.size() > 0){
            allMatch = checkAllMatch(inListStr, condition, ConditionConfig.STRING);
        }
        if(allMatch && inRangeLong != null && inRangeLong.size() > 0){
            allMatch = checkAllMatch(inRangeLong, condition, ConditionConfig.LONG);
        }
        if(allMatch && inRangeFloat != null && inRangeFloat.size() > 0){
            allMatch = checkAllMatch(inRangeFloat, condition, ConditionConfig.FLOAT);
        }
        if(allMatch && inListObject != null && inListObject.size() > 0){
            allMatch = checkAllMatch(inListObject, condition, ConditionConfig.OBJECT);
        }
        if(allMatch && inRangeDuration != null && inRangeDuration.size() > 0){
            allMatch = checkAllMatch(inRangeDuration, condition, ConditionConfig.DURATION);
        }
        if(allMatch && inListDuration != null && inListDuration.size() > 0){
            allMatch = checkAllMatch(inListDuration, condition, ConditionConfig.DURATION);
        }
        return allMatch;
    }

    public <T> boolean checkAllMatch(HashMap<String, List<T>> map, ConditionGame condition, String conditionType){
        return map.entrySet().stream().allMatch(stringStringEntry -> {
            List<T> value = stringStringEntry.getValue();
            String key = stringStringEntry.getKey();
            return value.stream().allMatch(val -> condition.isValidAll(key, val, conditionType));
        });

    }
}
