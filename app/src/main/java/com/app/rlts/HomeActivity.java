package com.app.rlts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG_FRAGMENT_TIMELOG = "tag_frag_timelog";
    private static final String TAG_FRAGMENT_NOTIFICATION = "tag_frag_notification";
    private static final String TAG_FRAGMENT_RESOURCES = "tag_frag_resources";

    private BottomNavigationView bottomNavigationView;

    // Maintains a list of Fragments for {@link BottomNavigationView}
    private List<TimelogFragment> fragments = new ArrayList<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.timelog:
                                switchFragment(0, TAG_FRAGMENT_TIMELOG);
                                return true;
                            case R.id.notification:
                                switchFragment(1, TAG_FRAGMENT_NOTIFICATION);
                                return true;
                            case R.id.resources:
                                switchFragment(2, TAG_FRAGMENT_RESOURCES);
                                return true;
                        }
                        return false;
                    }
                });

        buildFragmentsList();

        // Set the 0th Fragment to be displayed by default.
        switchFragment(0, TAG_FRAGMENT_TIMELOG);
    }

    private TimelogFragment buildFragment(String title) {
        TimelogFragment fragment = new TimelogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TimelogFragment.ARG_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void buildFragmentsList() {
        TimelogFragment timelogFragment = buildFragment("Timelog");
        TimelogFragment notificationFragment = buildFragment("Notification");
        TimelogFragment resourcesFragment = buildFragment("Resources");

        fragments.add(timelogFragment);
        fragments.add(notificationFragment);
        fragments.add(resourcesFragment);
    }

    private void switchFragment(int pos, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentholder, fragments.get(pos), tag)
                .commit();
    }
}
