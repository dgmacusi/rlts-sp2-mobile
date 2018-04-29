package com.app.rlts.task;

import android.net.Uri;
import android.os.AsyncTask;

import com.app.rlts.activity.LoginActivity;
import com.app.rlts.entity.Timelog;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncAddTimelogTask extends AsyncTask<String, String, String> {

    public Timelog timelog = null;
    HttpURLConnection conn;
    URL url = null;

    public AsyncAddTimelogTask(Timelog timelog) {
        this.timelog = timelog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            url = new URL("http://192.168.0.104:3000/addTimelog/web");
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }

        try{
            // setup HttpURLConnection class to send and receive data
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(LoginActivity.READ_TIMEOUT);
            conn.setConnectTimeout(LoginActivity.CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("date", this.timelog.getDate())
                    .appendQueryParameter("time", this.timelog.getTime())
                    .appendQueryParameter("entryType", this.timelog.getEntryType())
                    .appendQueryParameter("locationName", this.timelog.getLocationName())
                    .appendQueryParameter("username", this.timelog.getUsername());
            String query = builder.build().getEncodedQuery();

            // open connection for sending data
            OutputStream output = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));

            writer.write(query);
            writer.flush();
            writer.close();
            output.close();
            conn.connect();

        }catch(Exception e1){
            e1.printStackTrace();
            return e1.getMessage();
        }

        try {
            int response = conn.getResponseCode();
        } catch(Exception e2) {
            e2.printStackTrace();
            return e2.getMessage();
        }finally {
            conn.disconnect();
        }

        return "Adding timelog success";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

}
