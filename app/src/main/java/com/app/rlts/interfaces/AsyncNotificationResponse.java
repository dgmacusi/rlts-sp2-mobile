package com.app.rlts.interfaces;

import com.app.rlts.entity.Notification;

import java.util.ArrayList;

public interface AsyncNotificationResponse {

    public void retrieveNotifications(ArrayList<Notification> notifications, String type);
}
