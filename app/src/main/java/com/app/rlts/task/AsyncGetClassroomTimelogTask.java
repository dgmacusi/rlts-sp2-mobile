package com.app.rlts.task;

import android.net.Uri;
import android.os.AsyncTask;

import com.app.rlts.activity.LoginActivity;
import com.app.rlts.entity.Timelog;
import com.app.rlts.interfaces.AsyncTimelogResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncGetClassroomTimelogTask extends AsyncTask<String, String,String> {

    ArrayList<Timelog> classroomTimelog = new ArrayList<>();
    public AsyncTimelogResponse delegate;

    public AsyncGetClassroomTimelogTask(AsyncTimelogResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection conn;
        URL url = null;

        try {
            url = new URL("http://192.168.0.104:3000/getClassroomTimelog/web");
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        try {

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(LoginActivity.READ_TIMEOUT);
            conn.setConnectTimeout(LoginActivity.CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("date", params[0])
                    .appendQueryParameter("gradeLevel", params[1])
                    .appendQueryParameter("section", params[2]);
            String query = builder.build().getEncodedQuery();

            OutputStream ouput = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ouput, "UTF-8"));

            writer.write(query);
            writer.flush();
            writer.close();
            ouput.close();
            conn.connect();

        } catch (Exception e1) {
            e1.printStackTrace();
            return e1.getMessage();
        }

        try {

            int response_code = conn.getResponseCode();

            if (response_code == HttpURLConnection.HTTP_OK) {

                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                StringBuilder builder = new StringBuilder();
                String inputString;

                while ((inputString = reader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONObject topLevel = new JSONObject(builder.toString());
                JSONArray timelogArray = topLevel.getJSONArray("timelogArray");

                for (int i = 0; i < timelogArray.length(); i++) {
                    JSONObject t = timelogArray.getJSONObject(i);

                    String date = t.getString("date");
                    String time = t.getString("time");
                    String entryType = t.getString("entryType");
                    String locationName = t.getString("locationName");
                    String fullName = t.getString("fullName");

                    Timelog timelog = new Timelog(date, time, entryType, locationName, fullName);
                    classroomTimelog.add(timelog);
                }

                return "Retrieving success";
            } else {
                return "response_code not OK";
            }

        } catch (Exception e2) {
            e2.printStackTrace();
            return e2.getMessage();
        } finally {
            conn.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(classroomTimelog.size() > 0){
            delegate.retrieveTimelog(classroomTimelog);
        }else{
            classroomTimelog.add(new Timelog(s, " "," ", "No timelogs found.", " "));
            delegate.retrieveTimelog(classroomTimelog);
        }
    }
}
