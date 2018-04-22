package com.app.rlts.activity;

import android.app.Notification;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.app.rlts.R;
import com.app.rlts.entity.Beacon;
import com.app.rlts.entity.Timelog;
import com.app.rlts.fragment.NotificationFragment;
import com.app.rlts.fragment.ResourcesFragment;
import com.app.rlts.fragment.TimelogFragment;
import com.app.rlts.interfaces.AsyncResponse;
import com.app.rlts.logic.SessionManager;
import com.app.rlts.service.implementation.TimelogServiceImpl;
import com.app.rlts.task.AsyncGetBeaconsTask;
import com.estimote.internal_plugins_api.cloud.CloudCredentials;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, AsyncResponse {

    SessionManager session;
    private ProximityObserver proximityObserver;
    ArrayList<Beacon> beaconArray = new ArrayList<Beacon>();

    String username;

    Calendar calendar;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;

    TextView check2View;
    TextView beaconCheckView;
    TextView dateCheckView;
    TextView timeCheckView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new SessionManager(getApplicationContext());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        String auth_status = user.get(SessionManager.IS_LOGIN);
        username = user.get(SessionManager.KEY_NAME);
        String type = user.get(SessionManager.KEY_TYPE);

        check2View = (TextView) findViewById(R.id.check2);
        beaconCheckView = (TextView) findViewById(R.id.check_beacon);
        dateCheckView = (TextView) findViewById(R.id.check_date);
        timeCheckView = (TextView) findViewById(R.id.check_time);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        timeFormat = new SimpleDateFormat("HH:mm:ss");

        // loading default fragment
        loadFragment(new TimelogFragment());

        new AsyncGetBeaconsTask(this).execute();

        createProximityZone();
        startProximityObserver();
    }

    @Override
    public void retrieveBeacons(ArrayList<Beacon> bList){

        try{
            for (int i = 0; i < bList.size(); i++) {
                this.beaconArray.add(bList.get(i));
            }

            this.check2View.setText(String.valueOf(beaconArray.size()));
        }catch (Exception e){
            this.check2View.setText(e.getMessage());
        }
    }

    private void createProximityZone(){

        CloudCredentials cloudCredentials =
                new EstimoteCloudCredentials(getString(R.string.app_id), getString(R.string.app_token));

        // create the Proximity Observer
        this.proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), (EstimoteCloudCredentials) cloudCredentials)
                        .withOnErrorAction(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                //tv.setText(R.string.proximity_observer_error);
                                //Toast.makeText(MainActivity.this, R.string.proximity_observer_error, Toast.LENGTH_LONG);
                                Log.e("app", "proximity observer error" + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .withScannerInForegroundService(new Notification())
                        .build();

        // create proximity zone objects
        ProximityZone allBeacons = this.proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("uuid", "B9407F30-F5F8-466E-AFF9-25556B57FE6D")
                .inFarRange()
                .withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {

                        calendar = Calendar.getInstance();
                        String date = dateFormat.format(calendar.getTime());
                        String time = timeFormat.format(calendar.getTime());

                        TimelogServiceImpl timelogServiceImpl = new TimelogServiceImpl();

                        for (int i = 0; i < beaconArray.size(); i++) {
                            if (String.valueOf(beaconArray.get(i).getMinor()).equals(attachment.getPayload().get("minor")) && String.valueOf(beaconArray.get(i).getMajor()).equals(attachment.getPayload().get("major"))) {
                                Timelog timelog = new Timelog(date, time, "enter", beaconArray.get(i).getLocationName(), username);
                                //timelogServiceImpl.addTimelogToWeb(timelog);
                            }
                        }

                        new HomeActivity.AsyncData().execute(date,time,"enter","location",username); // date time entrytype locationname username

                        beaconCheckView.setText(R.string.welcome);
                        timeCheckView.setText(time);
                        dateCheckView.setText(date);

                        //Toast.makeText(MainActivity.this, getString(R.string.welcome), Toast.LENGTH_LONG);
                        Log.d("app", "Welcome!");
                        return null;
                    }
                })
                .withOnExitAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {

                        calendar = Calendar.getInstance();
                        String date = dateFormat.format(calendar.getTime());
                        String time = timeFormat.format(calendar.getTime());

                        TimelogServiceImpl timelogServiceImpl = new TimelogServiceImpl();

                        for (int i = 0; i < beaconArray.size(); i++) {
                            if (String.valueOf(beaconArray.get(i).getMinor()).equals(attachment.getPayload().get("minor")) && String.valueOf(beaconArray.get(i).getMajor()).equals(attachment.getPayload().get("major"))) {
                                Timelog timelog = new Timelog(date, time, "enter", beaconArray.get(i).getLocationName(), username);
                                //timelogServiceImpl.addTimelogToWeb(timelog);
                            }
                        }

                        new HomeActivity.AsyncData().execute(date,time,"exit","location",username);

                        beaconCheckView.setText(R.string.bye);
                        timeCheckView.setText(time);
                        dateCheckView.setText(date);

                        //Toast.makeText(MainActivity.this, R.string.bye, Toast.LENGTH_LONG);
                        Log.d("app", "Bye bye!");
                        return null;
                    }
                })
                .create();
        this.proximityObserver.addProximityZone(allBeacons);

        /*ProximityZone major2780 = this.proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("major", "2780")
                .inNearRange()
                .withOnChangeAction(new Function1<List<? extends ProximityAttachment>, Unit>() {
                    @Override
                    public Unit invoke(List<? extends ProximityAttachment> attachments) {
                        List<String> location = new ArrayList<>();
                        for (ProximityAttachment attachment : attachments) {
                            location.add(attachment.getPayload().get("minor"));
                        }
                        Log.d("app", "Nearby location: " + location);
                        return null;
                    }
                })
                .create();
        this.proximityObserver.addProximityZone(major2780);*/

    }

    private void startProximityObserver(){
        // request requirements permission
        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override public Unit invoke() {
                                //tv.setText(R.string.reqs_fulfilled);
                                //Toast.makeText(MainActivity.this, R.string.reqs_fulfilled, Toast.LENGTH_LONG);
                                Log.d("app", "requirements fulfilled");
                                proximityObserver.start();
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override public Unit invoke(List<? extends Requirement> requirements) {
                                //tv.setText(R.string.reqs_missing);
                                //Toast.makeText(MainActivity.this, R.string.reqs_missing, Toast.LENGTH_LONG);
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override public Unit invoke(Throwable throwable) {
                                //tv.setText(R.string.reqs_error);
                                //Toast.makeText(MainActivity.this, R.string.reqs_error, Toast.LENGTH_LONG);
                                Log.e("app", "requirements error: " + throwable);
                                return null;
                            }
                        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentholder_nav, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.timelog:
                fragment = new TimelogFragment();
                return loadFragment(fragment);
            case R.id.notification:
                fragment = new NotificationFragment();
                return loadFragment(fragment);
            case R.id.resources:
                fragment = new ResourcesFragment();
                return loadFragment(fragment);
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_logout:
                session.logOut();
                HomeActivity.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private class AsyncData extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {

            try{
                url = new URL("http://192.168.0.103:3000/login/web"); //change this
            }catch(MalformedURLException e){
                e.printStackTrace();
                return "exception";
            }

            try{
                conn = (HttpURLConnection)url.openConnection();
                //conn.setReadTimeout(READ_TIMEOUT);
                //conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("date", params[0])
                        .appendQueryParameter("time", params[1]);
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

            return null;
        }
    }
}
