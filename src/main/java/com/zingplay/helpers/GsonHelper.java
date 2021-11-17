package com.zingplay.helpers;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.zingplay.models.ValueCondition;
import com.zingplay.socket.SocketConst;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class GsonHelper {
    private static Gson gson = new Gson();
    public static String toJson(Object obj){
        return gson.toJson(obj);
    }
    public static HashMap<String, String> parseString(String json){
        try {
            HashMap<String, String> map = new HashMap<>();
            map = (HashMap<String, String>) gson.fromJson(json, map.getClass());
            return map;
        } catch (Exception e){
            return null;
        }
    }

    public static HashMap<String, Long> parseLong(String json){
        try {
            JSONObject jsonObj = new JSONObject(json);
            Iterator<String> iter = jsonObj.keys();
            HashMap<String, Long> map = new HashMap<>();
            while (iter.hasNext()) {
                String key = iter.next();
                map.put(key, jsonObj.optLong(key));
            }
            return map;
        } catch (Exception e){
            return null;
        }
    }

    public static HashMap<String, Float> parseFloat(String json){
        try {
            HashMap<String, Float> map = new HashMap<>();
            map = (HashMap<String, Float>) gson.fromJson(json, map.getClass());
            return map;
        } catch (Exception e){
            return null;
        }
    }

    public static HashMap<String, ValueCondition> parseObject(String json){
        try {
            HashMap<String, ValueCondition> res = new HashMap<>();
            JSONObject trackingMap = new JSONObject(json);
            Iterator<String> keys = trackingMap.keys();
            while (keys.hasNext()) {
                String trackingKey = keys.next();
                ValueCondition valueCondition = new ValueCondition();
                JSONObject tracking = trackingMap.getJSONObject(trackingKey);
                Iterator<String> mapNameKey = tracking.keys();
                while (mapNameKey.hasNext()) {
                    String mapName = mapNameKey.next();
                    JSONObject mapParams = tracking.getJSONObject(mapName);
                    Iterator<String> paramKey = mapParams.keys();
                    while (paramKey.hasNext()) {
                        String paramName = paramKey.next();
                        switch (mapName) {
                            case SocketConst.MAP_STRING:
                                valueCondition.setParamTracking(paramName, mapParams.getString(paramName));
                                break;
                        }
                    }
                }
                res.put(trackingKey, valueCondition);
            }
            return res;
        } catch (Exception e){
            return null;
        }
    }


}
