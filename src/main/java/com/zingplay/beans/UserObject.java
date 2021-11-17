package com.zingplay.beans;

import java.io.Serializable;
import java.util.Date;

public class UserObject implements Serializable {
    private String idObject;

    private String userId;
    public String getIdObject() {
        return idObject;
    }

    public void setIdObject(String idObject) {
        this.idObject = idObject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserObject{" +
                "idObject='" + idObject + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
