package com.app.rlts.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.app.rlts.R;
import com.app.rlts.entity.Beacon;
import com.app.rlts.entity.Timelog;
import com.app.rlts.fragment.NotificationFragment;
import com.app.rlts.fragment.ResourcesFragment;
import com.app.rlts.fragment.TimelogFragment;
import com.app.rlts.interfaces.AsyncNotificationResponse;
import com.app.rlts.interfaces.AsyncResponse;
import com.app.rlts.manager.SessionManager;
import com.app.rlts.task.AsyncAddTimelogTask;
import com.app.rlts.task.AsyncGetBeaconsTask;
import com.app.rlts.task.AsyncGetNotificationsTask;
import com.estimote.internal_plugins_api.cloud.CloudCredentials;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, AsyncResponse, AsyncNotificationResponse {

    SessionManager session;
    private ProximityObserver proximityObserver;

    ArrayList<Beacon> beaconArray = new ArrayList<Beacon>();
    ArrayList<Integer> currentLocation = new ArrayList<Integer>();
    ArrayList<Integer> receivedNotifications = new ArrayList<Integer>();

    String username;

    Calendar calendar;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;

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

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");

        // loading default fragment
        loadFragment(new TimelogFragment());

        new AsyncGetBeaconsTask(this).execute();

        callAsynchronousTask();

        //oreoNotification("http://www.google.com",0, "me", "test", "test");
    }

    public void oreoNotification(String downloadLink, int notif_id, String sender, String title, String text) {
        // set an id for the notification so it can be updated

        String channel_id = "channel_id";
        CharSequence name = getString(R.string.channel_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        PendingIntent contentIntent = null;

        if(downloadLink != null){
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink));

            contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        }

        // create a notification and set the notification channel
        Notification notification = new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.drawable.ic_schedule_black_24dp)
                .setContentTitle(getString(R.string.from) + " " + sender)
                .setContentText(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(title + "\n\n" + text))
                .setContentIntent(contentIntent)
                .build();

        //notification.setLatestEventInfo(getApplicationContext(), title, text, contentIntent);

        // get an instance of notification manager service
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            manager.createNotificationChannel(channel);
        }

        manager.notify(notif_id, notification);
    }

    /*public void nonOreoNotification(String title, String text){

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

    }*/

    @Override
    public void retrieveBeacons(ArrayList<Beacon> bList) {

        try {
            beaconArray.clear();
            for (int i = 0; i < bList.size(); i++) {
                if (!(bList.get(i).getLocationName().equalsIgnoreCase("NA"))) {
                    this.beaconArray.add(bList.get(i));
                }
            }

            createProximityZone();
            startProximityObserver();
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
    }

    @Override
    public void retrieveNotifications(ArrayList<com.app.rlts.entity.Notification> nList, String type) {


        for (int i = 0; i < nList.size(); i++) {
            if (currentLocation.size() > 0 && currentLocation.get(currentLocation.size() - 1) == nList.get(i).getBeaconId()) {
                if (type.equalsIgnoreCase("enter")) {
                    oreoNotification(nList.get(i).getDownloadLink(), nList.get(i).getNotificationId(), nList.get(i).getSender(), nList.get(i).getTitle(), nList.get(i).getBody());
                } else if (type.equalsIgnoreCase("real-time")) {
                    if (!(receivedNotifications.contains(nList.get(i).getNotificationId()))) {
                        oreoNotification(nList.get(i).getDownloadLink(), nList.get(i).getNotificationId(), nList.get(i).getSender(), nList.get(i).getTitle(), nList.get(i).getBody());
                        receivedNotifications.add(nList.get(i).getNotificationId());
                    }
                }

            }
        }
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new AsyncGetNotificationsTask(HomeActivity.this).execute("real-time");
                        } catch (Exception e) {
                            e.printStackTrace();
                            ;
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000);
    }

    private void createProximityZone() {

        CloudCredentials cloudCredentials =
                new EstimoteCloudCredentials(getString(R.string.app_id), getString(R.string.app_token));

        // create the Proximity Observer
        this.proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), (EstimoteCloudCredentials) cloudCredentials)
                        .withOnErrorAction(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "proximity observer error" + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .withScannerInForegroundService(new Notification())
                        .build();

        for (int i = 0; i < this.beaconArray.size(); i++) {
            ProximityZone beaconZone = this.proximityObserver.zoneBuilder()
                    .forAttachmentKeyAndValue("major", String.valueOf(this.beaconArray.get(i).getMajor()))
                    .inNearRange()
                    .withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
                        @Override
                        public Unit invoke(ProximityAttachment attachment) {

                            /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                oreoNotification("Notification", "Hello from the other side.");
                            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                nonOreoNotification("Notification", "Hello from the other side.");
                            }*/

                            calendar = Calendar.getInstance();
                            String date = dateFormat.format(calendar.getTime());
                            String time = timeFormat.format(calendar.getTime());

                            for (int i = 0; i < beaconArray.size(); i++) {
                                if (String.valueOf(beaconArray.get(i).getMinor()).equals(attachment.getPayload().get("minor")) && String.valueOf(beaconArray.get(i).getMajor()).equals(attachment.getPayload().get("major"))) {

                                    Timelog timelog = new Timelog(date, time, "enter", beaconArray.get(i).getLocationName(), username);
                                    new AsyncAddTimelogTask(timelog).execute();

                                    if (!(currentLocation.contains(beaconArray.get(i).getBeaconId()))) {
                                        currentLocation.add(beaconArray.get(i).getBeaconId());
                                    }

                                    new AsyncGetNotificationsTask(HomeActivity.this).execute("enter");
                                }
                            }

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

                            for (int i = 0; i < beaconArray.size(); i++) {
                                if (String.valueOf(beaconArray.get(i).getMinor()).equals(attachment.getPayload().get("minor")) && String.valueOf(beaconArray.get(i).getMajor()).equals(attachment.getPayload().get("major"))) {
                                    Timelog timelog = new Timelog(date, time, "exit", beaconArray.get(i).getLocationName(), username);
                                    new AsyncAddTimelogTask(timelog).execute();

                                    currentLocation.removeAll(Arrays.asList(beaconArray.get(i).getBeaconId()));
                                }
                            }

                            Log.d("app", "Bye bye!");
                            return null;
                        }
                    })
                    .create();


            this.proximityObserver.addProximityZone(beaconZone);
        }
    }

    /*private void createProximityZone(){

        CloudCredentials cloudCredentials =
                new EstimoteCloudCredentials(getString(R.string.app_id), getString(R.string.app_token));

        // create the Proximity Observer
        this.proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), (EstimoteCloudCredentials) cloudCredentials)
                        .withOnErrorAction(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
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

                        for (int i = 0; i < beaconArray.size(); i++) {
                            if (String.valueOf(beaconArray.get(i).getMinor()).equals(attachment.getPayload().get("minor")) && String.valueOf(beaconArray.get(i).getMajor()).equals(attachment.getPayload().get("major"))) {
                                Timelog timelog = new Timelog(date, time, "enter", beaconArray.get(i).getLocationName(), username);
                                new AsyncAddTimelogTask(timelog).execute();
                                timeCheckView.setText("enter");
                                String name = beaconArray.get(i).getBeaconName();
                                dateCheckView.setText(name);

                            }
                        }
                        beaconCheckView.setText(R.string.welcome);

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

                        for (int i = 0; i < beaconArray.size(); i++) {
                            if (String.valueOf(beaconArray.get(i).getMinor()).equals(attachment.getPayload().get("minor")) && String.valueOf(beaconArray.get(i).getMajor()).equals(attachment.getPayload().get("major"))) {
                                Timelog timelog = new Timelog(date, time, "exit", beaconArray.get(i).getLocationName(), username);
                                new AsyncAddTimelogTask(timelog).execute();
                                timeCheckView.setText("exit");
                                String name = beaconArray.get(i).getBeaconName();
                                dateCheckView.setText(name);
                            }
                        }
                        beaconCheckView.setText(R.string.bye);

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
        this.proximityObserver.addProximityZone(major2780);

    }*/

    private void startProximityObserver() {
        // request requirements permission
        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                proximityObserver.start();
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override
                            public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
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
}
