package com.app.rlts.entity;

import java.util.ArrayList;

public class Notification {

    private String date;
    private String time;
    private String title;
    private ArrayList<String> sendTo; //arraylist dapat to
    private String body;

    public Notification(String date, String time, String title, ArrayList<String> sendTo, String body){

        this.date = date;
        this.time = time;
        this.title = title;
        this.sendTo = sendTo;
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getSendTo() {
        return sendTo;
    }

    public String getBody() {
        return body;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSendTo(ArrayList<String> sendTo) {
        this.sendTo = sendTo;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
