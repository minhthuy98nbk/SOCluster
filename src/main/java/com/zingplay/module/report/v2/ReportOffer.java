package com.zingplay.module.report.v2;

import java.io.Serializable;

public class ReportOffer implements Serializable {
    private String country;
    private String currency;
    private long users;
    private float rev;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getUsers() {
        return users;
    }

    public void setUsers(long users) {
        this.users = users;
    }

    public float getRev() {
        return rev;
    }

    public void setRev(float rev) {
        this.rev = rev;
    }
}
