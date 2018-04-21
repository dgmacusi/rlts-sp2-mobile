package com.app.rlts.service.implementation;

import android.net.Uri;
import android.os.AsyncTask;

import com.app.rlts.activity.LoginActivity;
import com.app.rlts.entity.Timelog;
import com.app.rlts.service.TimelogService;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TimelogServiceImpl implements TimelogService {

    @Override
    public void addTimelogToWeb(Timelog timelog) {
        new AsyncAddTimelog(timelog).execute("timelog");
    }

    private class AsyncAddTimelog extends AsyncTask<String, String, String> {
        private Timelog timelog;
        HttpURLConnection conn;
        URL url = null;

        AsyncAddTimelog(Timelog timelog) {
            this.timelog = timelog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try{
                url = new URL("http://192.168.0.103:3000/addTimelog/web"); //change this
            }catch(MalformedURLException e){
                e.printStackTrace();
                return "exception";
            }

            try{
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
                return "exception";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {}
    }
}
