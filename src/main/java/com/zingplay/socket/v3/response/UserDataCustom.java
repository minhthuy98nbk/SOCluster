package com.zingplay.socket.v3.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDataCustom implements Serializable {
    private String version = "1.0";
    private String userId;
    private long timeCurrent;
    private List<DataCustom> dataCustoms;

    public UserDataCustom(String userId) {
        this.userId = userId;
        this.dataCustoms = new ArrayList<>();
    }

    public long getTimeCurrent() {
        return timeCurrent;
    }

    public void setTimeCurrent(long timeCurrent) {
        this.timeCurrent = timeCurrent;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<DataCustom> getDataCustoms() {
        return dataCustoms;
    }

    public void setDataCustoms(List<DataCustom> dataCustoms) {
        this.dataCustoms = dataCustoms;
    }

    @Override
    public String toString() {
        return "UserDataCustom{" +
                "version='" + version + '\'' +
                ", userId='" + userId + '\'' +
                ", timeCurrent=" + timeCurrent +
                ", dataCustoms=" + dataCustoms +
                '}';
    }
}
