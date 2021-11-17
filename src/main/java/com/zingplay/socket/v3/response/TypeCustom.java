package com.zingplay.socket.v3.response;

public enum TypeCustom {
    GIFT;

    public static TypeCustom getTypeByName(String name) {
        for (TypeCustom type : TypeCustom.values()) {
            if (name.equals(type.name())) {
                return type;
            }
        }
        return null;
    }
}
