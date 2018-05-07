package com.app.rlts.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rlts.R;
import com.app.rlts.manager.SessionManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText usernameField;
    private EditText passwordField;

    SessionManager session;
    HashMap<String, String> user;

    private String username;
    private String type;

    TextView checkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());

        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);

        checkView = (TextView) findViewById(R.id.check);

        user = session.getUserDetails();

        checkView.setText(user.get(SessionManager.IS_LOGIN));

        session.checkLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkView.setText(user.get(SessionManager.IS_LOGIN));

        session.checkLogin();
    }

    // triggers when LOGIN button is clicked
    public void logIn(View arg0) {

        // get text from email and passord field
        final String email = usernameField.getText().toString();
        final String password = passwordField.getText().toString();

        // initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(email,password);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            oreoNotification("Notification", "Hello from the other side.");
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            nonOreoNotification("Notification", "Hello from the other side.");
        }
    }

    public void oreoNotification(String title, String text){

        // set an id for the notification so it can be updated
        int notif_id = 1;

        String channel_id = "channel_id";
        CharSequence name = getString(R.string.channel_name);
        int importance = android.app.NotificationManager.IMPORTANCE_HIGH;

        // create a notification and set the notification channel
        Notification notification = new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.drawable.ic_schedule_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .build();

        // get an instance of notification manager service
        android.app.NotificationManager manager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //manager.createNotificationChannel(channel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            manager.createNotificationChannel(channel);
        }

        manager.notify(notif_id, notification);
    }

    public void nonOreoNotification(String title, String text){

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] {intent}, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_schedule_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        android.app.NotificationManager manager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);

    }

    private class AsyncLogin extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // method will be running on UI thread
            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try{
                url = new URL("http://192.168.1.12:3000/login/web");
            }catch(MalformedURLException e){
                e.printStackTrace();
                return "exception";
            }

            try{

                // setup HttpURLConnection class to send and receive data
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // open connection for sending data
                OutputStream output = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));

                writer.write(query);
                writer.flush();
                writer.close();
                output.close();
                conn.connect();

            }catch(IOException e1){
                e1.printStackTrace();
                return "exception";
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
                    JSONObject user = topLevel.getJSONObject("user");

                    username = user.getString("username");
                    type = user.getString("type");
                    String auth_status = String.valueOf(user.getBoolean("authenticated"));

                    return(auth_status);

                }else{
                    return "unsuccesful";
                }

            }catch (Exception e2){
                e2.printStackTrace();
                return "exception";
            }finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            // method will be running on UI thread
            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true")){

                session.loginSession(username, type);

                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                LoginActivity.this.finish();

            }else if(result.equalsIgnoreCase("false")){

                // if username and password does not match display a error message
                Toast.makeText(LoginActivity.this, R.string.invalid_email_password, Toast.LENGTH_LONG).show();

            }else if(result.equalsIgnoreCase("exception")){

                Toast.makeText(LoginActivity.this, R.string.connection_problem, Toast.LENGTH_LONG).show();
            }
        }
    }
}
