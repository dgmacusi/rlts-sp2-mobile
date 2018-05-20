package com.app.rlts.entity;

import java.util.ArrayList;

public class WebNotification {

    private String date;
    private String time;
    private String title;
    private ArrayList<String> sendTo;
    private String body;
    private String username;
    private String downloadLink;

    public WebNotification(String date, String time, String title, ArrayList<String> sendTo, String body, String username, String downloadLink){

        this.date = date;
        this.time = time;
        this.title = title;
        this.sendTo = sendTo;
        this.body = body;
        this.username = username;
        this.downloadLink = downloadLink;
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

    public String getUsername() {
        return username;
    }

    public String getDownloadLink() {
        return downloadLink;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
