package com.app.rlts.task;

import android.os.AsyncTask;

import com.app.rlts.activity.LoginActivity;
import com.app.rlts.entity.Beacon;
import com.app.rlts.interfaces.AsyncResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AsyncGetBeaconsTask extends AsyncTask<String, String, String> {
    public AsyncResponse delegate = null;
    ArrayList<Beacon> beacons = new ArrayList<Beacon>();

    public AsyncGetBeaconsTask(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection conn;
        URL url = null;

        try{
            url = new URL("http://192.168.0.104:3000/getAllBeacons/web");
        }catch(MalformedURLException e){
            e.printStackTrace();
            return e.getMessage();
        }

        try{

            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(LoginActivity.READ_TIMEOUT);
            conn.setConnectTimeout(LoginActivity.CONNECTION_TIMEOUT);
            conn.setRequestMethod("GET");

            conn.connect();

        }catch(Exception e1){
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

                while ((inputString = reader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONObject topLevel = new JSONObject(builder.toString());
                JSONArray beaconArray = topLevel.getJSONArray("beaconArray");

                for (int i = 0; i < beaconArray.length(); i++) {
                    JSONObject b = beaconArray.getJSONObject(i);
                    String locationName = b.getString("locationName");
                    String beaconName = b.getString("beaconName");
                    int minor = b.getInt("minor");
                    int major = b.getInt("major");
                    String uuid = b.getString("uuid");
                    String type = b.getString("type");

                    Beacon beacon = new Beacon(locationName, beaconName, minor, major, uuid, type);
                    beacons.add(beacon);
                }

                return "Retrieving success";

            }else{
                return "response_code not OK";
            }

        }catch (Exception e2){
            e2.printStackTrace();
            return e2.getMessage();
        }finally {
            conn.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        if (beacons.size() > 0) {
            delegate.retrieveBeacons(beacons);
        } else {
            beacons.add(new Beacon(result, "test beacon", 0, 0, "test uuid", "test type"));
            delegate.retrieveBeacons(beacons);
        }
    }
}
