package com.app.rlts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
        ArrayList<Timelog> timelogs = (ArrayList<Timelog>) intent.getSerializableExtra("beaconArray");

        //ArrayList<Timelog> timelogs;

        /*Timelog one = new Timelog("24-04-2018", "06:16:54", "enter", "library", "dmacusi");
        Timelog two = new Timelog("24-04-2018", "06:25:32", "exit", "library", "dmacusi");
        Timelog three = new Timelog("24-04-2018", "06:35:38", "enter", "canteen", "dmacusi");
        Timelog four = new Timelog("24-04-2018", "06:40:13", "exit", "canteen", "dmacusi");

        timelogs.add(one);
        timelogs.add(two);
        timelogs.add(three);
        timelogs.add(four);*/

        TextView name = (TextView) findViewById(R.id.timelog_name);
        if(result.equalsIgnoreCase("studentteacher")){
            name.setText(timelogs.get(0).getUsername());
        }else{
            name.setText(timelogs.get(0).getLocationName());
        }

        TextView date = (TextView) findViewById(R.id.timelog_date);
        date.setText(timelogs.get(0).getDate());

        TableLayout tableLayout = (TableLayout) findViewById(R.id.timelog_table);

        TextView header = (TextView) findViewById(R.id.header);
        if(result.equalsIgnoreCase("studentteacher")){
            header.setText(new StringBuilder().append(header.getText()).append(getString(R.string.location)).toString());
        }else{
            header.setText(new StringBuilder().append(header.getText()).append(getString(R.string.user_name)).toString());
        }

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        layoutParams.setMargins(50, 1, 50, 1);

        for (int i = 0; i < timelogs.size(); i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(layoutParams);
            row.setBackgroundResource(R.drawable.border);

            TextView time = new TextView(this);
            time.setText(timelogs.get(i).getTime());
            time.setTextColor(getResources().getColor(R.color.black));
            time.setLayoutParams(layoutParams);

            TextView entryType = new TextView(this);
            entryType.setText(timelogs.get(i).getTime());
            entryType.setTextColor(getResources().getColor(R.color.black));
            entryType.setLayoutParams(layoutParams);

            TextView third = new TextView(this);
            third.setText(timelogs.get(i).getTime());
            third.setTextColor(getResources().getColor(R.color.black));
            third.setLayoutParams(layoutParams);

            if(result.equalsIgnoreCase("studentteacher")){
                third.setText(timelogs.get(i).getLocationName());
            }else{
                third.setText(timelogs.get(i).getUsername());
            }

            row.addView(time);
            row.addView(entryType);
            row.addView(third);
            tableLayout.addView(row);
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
