package com.app.rlts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rlts.R;
import com.app.rlts.entity.Timelog;
import com.app.rlts.logic.SessionManager;

import java.util.ArrayList;

public class TimelogActivity extends AppCompatActivity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timelog);

        session = new SessionManager(getApplicationContext());

        LinearLayout layout = (LinearLayout) findViewById(R.id.timelog_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        String result = intent.getStringExtra("fragment");

        ArrayList<Timelog> timelogs = new ArrayList<Timelog>();

        Timelog one = new Timelog("24-04-2018", "06:16:54", "enter", "library", "dmacusi");
        Timelog two = new Timelog("24-04-2018", "06:25:32", "exit", "library", "dmacusi");
        Timelog three = new Timelog("24-04-2018", "06:35:38", "enter", "canteen", "dmacusi");
        Timelog four = new Timelog("24-04-2018", "06:40:13", "exit", "canteen", "dmacusi");

        timelogs.add(one);
        timelogs.add(two);
        timelogs.add(three);
        timelogs.add(four);

        TextView name = (TextView) findViewById(R.id.timelog_name);
        if(result.equalsIgnoreCase("studentteacher")){
            name.setText(timelogs.get(0).getUsername());
        }else{
            name.setText(timelogs.get(0).getLocationName());
        }

        TextView date = (TextView) findViewById(R.id.timelog_date);
        date.setText(timelogs.get(0).getDate());

        TextView header = (TextView) findViewById(R.id.timelog_header);
        if(result.equalsIgnoreCase("studentteacher")){
            header.setText(new StringBuilder().append(header.getText()).append(getString(R.string.location)).toString());
        }else{
            header.setText(new StringBuilder().append(header.getText()).append(getString(R.string.user_name)).toString());
        }

        for (int i = 0; i < timelogs.size(); i++) {

            TextView tv = new TextView(this);
            tv.setText(new StringBuilder().append(timelogs.get(i).getTime()).append(getString(R.string.tab)).append(timelogs.get(i).getEntryType()).toString());
            tv.setBackgroundResource(R.drawable.border);
            tv.setTextColor(getResources().getColor(R.color.black));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(50, 1, 50, 1);

            if(result.equalsIgnoreCase("studentteacher")){
                tv.setText(new StringBuilder().append(tv.getText()).append(getString(R.string.tab)).append(timelogs.get(i).getLocationName()).toString());
            }else{
                tv.setText(new StringBuilder().append(tv.getText()).append(getString(R.string.tab)).append(timelogs.get(i).getUsername()).toString());
            }

            tv.setLayoutParams(layoutParams);
            layout.addView(tv);
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
                TimelogActivity.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
