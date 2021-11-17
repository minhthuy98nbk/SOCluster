package com.zingplay.cron.beans.group_jobs;

public enum GroupId {
    RUN_OFFER(0),
    USER_GROUP(1),
    DELETE_USER(2),
    RUN_OFFER_TO_OBJECT(3),
    SCHEDULE_RUN_OFFER(4),

    UNDEFINED(-1),
    ;

    int code;

    GroupId(int code) {
        this.code = code;
    }

    public static GroupId fromCode(int code) {
        for (GroupId type : GroupId.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return UNDEFINED;
    }

    public int getCode() {
        return code;
    }
}
