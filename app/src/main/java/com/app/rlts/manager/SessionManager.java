package com.app.rlts.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.rlts.activity.HomeActivity;
import com.app.rlts.activity.LoginActivity;
import com.app.rlts.activity.NotificationActivity;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "RLTSPref";

    // all shared preferences keys
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_TYPE = "type";

    public SessionManager(Context context){

        this._context = context;
        prefs = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    // create login session
    public void loginSession(String name, String type){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_TYPE, type);

        editor.apply();
    }

    // get stored session data
    public HashMap<String, String> getUserDetails(){

        HashMap<String, String> user = new HashMap<String, String>();

        user.put(IS_LOGIN, String.valueOf(prefs.getBoolean(IS_LOGIN, false)));
        user.put(KEY_NAME, prefs.getString(KEY_NAME, null));
        user.put(KEY_TYPE, prefs.getString(KEY_TYPE, "student"));

        return user;
    }

    /*public boolean isLoggedIn(){
        return prefs.getBoolean(IS_LOGIN, false);
    }*/

    // check login method will check user login status
    // - if true redirect to home page
    // - else if false it will redirect user to login page
    public void checkLogin(){

        Boolean status = prefs.getBoolean(IS_LOGIN, false);
        String type = prefs.getString(KEY_TYPE, "student");

        if (type.equalsIgnoreCase("student") && status) {

            Intent i = new Intent(_context, NotificationActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);
        }else if (status){

            Intent i = new Intent(_context, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);
        }
    }

    // clear session details
    public void logOut(){

        // clear all data from shared preferences
        editor.clear();
        editor.apply();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }
}
