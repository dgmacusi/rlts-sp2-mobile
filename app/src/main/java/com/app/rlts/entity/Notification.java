package com.app.rlts.entity;

public class Notification {

   private int beaconId;
   private int notificationId;
   private String sender;
   private String title;
   private String body;

    public Notification(int beaconId, int notificationId, String sender, String title, String body){

        this.beaconId = beaconId;
        this.notificationId = notificationId;
        this.sender = sender;
        this.title = title;
        this.body = body;
    }

    public int getBeaconId() {
        return beaconId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public String getSender() {
        return sender;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setBeaconId(int beaconId) {
        this.beaconId = beaconId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
