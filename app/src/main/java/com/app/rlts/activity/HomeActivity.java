package com.app.rlts.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.app.rlts.R;
import com.app.rlts.session.SessionManager;
import com.app.rlts.fragment.NotificationFragment;
import com.app.rlts.fragment.ResourcesFragment;
import com.app.rlts.fragment.TimelogFragment;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    SessionManager session;

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
        String username = user.get(SessionManager.KEY_NAME);
        String type = user.get(SessionManager.KEY_TYPE);

        TextView check2View = (TextView) findViewById(R.id.check2);
        check2View.setText(user.get(SessionManager.KEY_TYPE));

        // loading default fragment
        loadFragment(new TimelogFragment());
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
