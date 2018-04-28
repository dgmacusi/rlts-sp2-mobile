package com.app.rlts.entity;

import java.io.Serializable;

public class Timelog implements Serializable{

    private String date;
    private String time;
    private String entryType;
    private String locationName;
    private String username;

    public Timelog(String date, String time, String entryType, String locationName, String username) {
        this.date = date;
        this.time = time;
        this.entryType = entryType;
        this.locationName = locationName;
        this.username = username;
    }

    public String getDate() {
        return this.date;
    }
    public String getTime() {
        return this.time;
    }
    public String getEntryType() {
        return this.entryType;
    }
    public String getLocationName() {
        return this.locationName;
    }
    public String getUsername() {
        return this.username;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
