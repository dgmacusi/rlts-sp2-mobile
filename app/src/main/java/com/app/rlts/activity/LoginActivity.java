package com.app.rlts.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.rlts.R;
import com.app.rlts.SessionManager;

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

public class LoginActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText usernameField;
    private EditText passwordField;

    SessionManager session;

    private String username;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());

        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
    }

    // triggers when LOGIN button is clicked
    public void logIn(View arg0) {

        // get text from email and passord field
        final String email = usernameField.getText().toString();
        final String password = passwordField.getText().toString();

        // initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(email,password);
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
                url = new URL("http://192.168.0.101:3000/login/web");
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
