package com.zingplay.socket.v3;

import com.zingplay.models.User;
import com.zingplay.models.ValueCondition;

public enum TrackingCommon {
//    public static String TimeCreate = "timeCreate";//long milli
//    public static String TimeOnline = "timeOnline";//long milli
//    public static String TimeCurrent = "timeCurrent";//long milli

    AgeUser             ("ageUser",         TypeUpdateParam.SET, TypeParam.DURATION),
    NumDayOffline       ("numDayOffline",   TypeUpdateParam.SET, TypeParam.DURATION),
    ChannelPayments     ("channelPayments", TypeUpdateParam.SET, TypeParam.STRING),
    TotalTimesPaid      ("totalTimesPaid",  TypeUpdateParam.SUM, TypeParam.LONG),
    TotalPaid           ("totalPaid",       TypeUpdateParam.SUM, TypeParam.LONG),
    LastPaidPack        ("lastPaidPack",    TypeUpdateParam.SET, TypeParam.OBJECT),
    currency            ("currency",        TypeUpdateParam.SET, TypeParam.STRING),
    cost                ("cost",            TypeUpdateParam.SET, TypeParam.FLOAT),
    ChannelIdx          ("channelIdx",      TypeUpdateParam.SET, TypeParam.STRING),
    TotalGame           ("totalGame",       TypeUpdateParam.SET, TypeParam.LONG),
    ;

    String text;
    TypeUpdateParam typeUpdateParam;
    TypeParam typeParam;

    TrackingCommon(String text, TypeUpdateParam typeUpdateParam, TypeParam typeParam) {
        this.text = text;
        this.typeUpdateParam = typeUpdateParam;
        this.typeParam = typeParam;
    }

    public String getText() {
        return text;
    }

    public TypeUpdateParam getTypeUpdateParam() {
        return typeUpdateParam;
    }

    public TypeParam getTypeParam() {
        return typeParam;
    }

    public static TrackingCommon getByText(String text){
        for (TrackingCommon type : TrackingCommon.values()) {
            if (text.equals(type.getText())) {
                return type;
            }
        }
        return null;
    }

    // string
    public static void setConstantTracking (User user, TrackingCommon trackingCommon, String value) {
        user.setTracking(trackingCommon.getText(), value);
    }

    // long / duration
    public static void setConstantTracking (User user, TrackingCommon trackingCommon, int value) {
        user.setTracking(trackingCommon.getText(), (long)value, trackingCommon.getTypeParam(), trackingCommon.getTypeUpdateParam());
    }

    public static void setConstantTracking (User user, TrackingCommon trackingCommon, long value) {
        user.setTracking(trackingCommon.getText(), value, trackingCommon.getTypeParam(), trackingCommon.getTypeUpdateParam());
    }

    public static void setConstantTracking (User user, TrackingCommon trackingCommon, Long value) {
        user.setTracking(trackingCommon.getText(), value, trackingCommon.getTypeParam(), trackingCommon.getTypeUpdateParam());
    }

    // float
    public static void setConstantTracking (User user, TrackingCommon trackingCommon, Float value) {
        user.setTracking(trackingCommon.getText(), value, trackingCommon.getTypeUpdateParam());
    }

    // object
    public static void setConstantTrackingObject (User user, TrackingCommon trackingCommon, String ... strings) {
        ValueCondition value = new ValueCondition();
        switch (trackingCommon){
            case LastPaidPack:
                value.setParamTracking(TrackingCommon.currency.getText(), strings[0]);
                value.setParamTracking(TrackingCommon.cost.getText(), strings[1]);
                break;
            default:
                value = null;
        }
        if (value != null) {
            user.setTracking(trackingCommon.getText(), value);
        }
    }
}
