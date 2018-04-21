package com.app.rlts.service.implementation;

import com.app.rlts.activity.LoginActivity;
import com.app.rlts.entity.Beacon;
import com.app.rlts.service.BeaconService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BeaconServiceImpl implements BeaconService {

    public BeaconServiceImpl(){

    }

	@Override
	public ArrayList<Beacon> getAllBeacons () {
		HttpURLConnection conn;
        URL url = null;
        ArrayList<Beacon> beacons = new ArrayList<Beacon>();

		try{
            url = new URL("http://192.168.0.103:3000/retrieveBeacons/web");
        }catch(MalformedURLException e){
            e.printStackTrace();
            return beacons;
        }

        try{

            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(LoginActivity.READ_TIMEOUT);
            conn.setConnectTimeout(LoginActivity.CONNECTION_TIMEOUT);
            conn.setRequestMethod("GET");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream output = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));

            writer.flush();
            writer.close();
            output.close();
            conn.connect();

        }catch(Exception e1){
            e1.printStackTrace();
            return beacons;
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
               	JSONArray beaconArray = topLevel.getJSONArray("beacons");

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
               	
                return beacons;

            }else{
                return beacons;
            }

        }catch (Exception e2){
            e2.printStackTrace();
            return beacons;
        }finally {
            conn.disconnect();
        }
	}
}