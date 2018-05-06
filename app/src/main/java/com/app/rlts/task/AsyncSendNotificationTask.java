package com.app.rlts.task;

import android.net.Uri;
import android.os.AsyncTask;

import com.app.rlts.activity.LoginActivity;
import com.app.rlts.entity.Notification;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncSendNotificationTask extends AsyncTask<String, String, String>{

    public Notification notification = null;
    HttpURLConnection conn;
    URL url = null;

    public AsyncSendNotificationTask(Notification notification){
        this.notification = notification;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        try{
            url = new URL("http://192.168.1.12:3000/sendNotification/web");
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }

        try{

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(LoginActivity.READ_TIMEOUT);
            conn.setConnectTimeout(LoginActivity.CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            String locations = this.notification.getSendTo().toString().replace("[","").replace("]","").replaceAll("\\s","").trim();

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("title", this.notification.getTitle())
                    .appendQueryParameter("send_to", locations)
                    .appendQueryParameter("body", this.notification.getBody());
            String query = builder.build().getEncodedQuery();

            OutputStream output = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));

            writer.write(query);
            writer.flush();
            writer.close();
            output.close();
            conn.connect();

        }catch (Exception e1){
            e1.printStackTrace();
            return e1.getMessage();
        }

        try{
            int response = conn.getResponseCode();
        } catch (Exception e2){
            e2.printStackTrace();
            return e2.getMessage();
        }finally {
            conn.disconnect();
        }

        return "Notification sent.";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
