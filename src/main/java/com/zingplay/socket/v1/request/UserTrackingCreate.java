package com.zingplay.socket.v1.request;

import com.zingplay.beans.UserTracking;

import java.util.List;

public class UserTrackingCreate {
    private List<UserTracking> users;

    public List<UserTracking> getUsers() {
        return users;
    }

    public void setUsers(List<UserTracking> users) {
        this.users = users;
    }
}
