package com.app.rlts.task;

import android.os.AsyncTask;

import com.app.rlts.activity.LoginActivity;
import com.app.rlts.entity.Notification;
import com.app.rlts.interfaces.AsyncNotificationResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncGetNotificationsTask extends AsyncTask<String, String, String>{

    public AsyncNotificationResponse delegate = null;
    ArrayList<Notification> notifications = new ArrayList<>();

    HttpURLConnection conn;
    URL url = null;

    public String type = null;

    public AsyncGetNotificationsTask(AsyncNotificationResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        type = params[0];

        try{
            url = new URL("http://192.168.22.13:3000/getAllNotifications/web");
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }

        try{

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(LoginActivity.READ_TIMEOUT);
            conn.setConnectTimeout(LoginActivity.READ_TIMEOUT);
            conn.setRequestMethod("GET");

            conn.connect();

        }catch (Exception e1){
            e1.printStackTrace();
            return e1.getMessage();
        }

        try {

            int response_code = conn.getResponseCode();

            if(response_code == HttpURLConnection.HTTP_OK){

                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                StringBuilder builder = new StringBuilder();
                String inputString;

                while((inputString = reader.readLine()) != null){
                    builder.append(inputString);
                }

                JSONObject topLevel = new JSONObject(builder.toString());
                JSONArray notificationArray = topLevel.getJSONArray("notificationArray");

                for(int i = 0; i < notificationArray.length(); i++){

                    JSONObject n = notificationArray.getJSONObject(i);

                    int notificationId = n.getInt("notificationId");
                    int beaconId = n.getInt("beaconId");
                    String sender = n.getString("sender");
                    String title = n.getString("title");
                    String body = n.getString("body");
                    String downloadLink = n.getString("downloadLink");

                    Notification notification = new Notification(beaconId, notificationId, sender, title, body, downloadLink);
                    notifications.add(notification);
                }

                return "Retrieving notifications: Success";
            }else{
                return "response_code not OK";
            }

        }catch (Exception e2){
            e2.printStackTrace();
            return e2.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(notifications.size() > 0){
            delegate.retrieveNotifications(notifications, type);
        }else{
            //notifications.add(new Notification(s, "test time", "test title", "test sendTo", "test body"));
            delegate.retrieveNotifications(notifications, type);
        }
    }
}
