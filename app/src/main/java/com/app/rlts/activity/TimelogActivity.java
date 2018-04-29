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
    ArrayList<Timelog> timelogArray;

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
        Bundle bundle = intent.getExtras();
        timelogArray = (ArrayList<Timelog>) bundle.getSerializable("timelogArray");

        TextView name = (TextView) findViewById(R.id.timelog_name);
        if(result.equalsIgnoreCase("studentteacher")){
            name.setText(timelogArray.get(0).getUsername());
        }else{
            name.setText(timelogArray.get(0).getLocationName());
        }

        TextView date = (TextView) findViewById(R.id.timelog_date);
        date.setText(timelogArray.get(0).getDate());

        TableLayout timelogLayout = (TableLayout) findViewById(R.id.timelog_table);

        TextView header = (TextView) findViewById(R.id.header);
        if(result.equalsIgnoreCase("studentteacher")){
            header.setText(new StringBuilder().append(header.getText()).append(getString(R.string.location)).toString());
        }else{
            header.setText(new StringBuilder().append(header.getText()).append(getString(R.string.name)).toString());
        }

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        layoutParams.setMargins(50, 1, 50, 1);

        for (int i = 0; i < timelogArray.size(); i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(layoutParams);
            row.setBackgroundResource(R.drawable.border);

            TextView time = new TextView(this);
            time.setText(timelogArray.get(i).getTime());
            time.setTextColor(getResources().getColor(R.color.black));
            time.setLayoutParams(layoutParams);

            TextView entryType = new TextView(this);
            entryType.setText(timelogArray.get(i).getEntryType());
            entryType.setTextColor(getResources().getColor(R.color.black));
            entryType.setLayoutParams(layoutParams);

            TextView third = new TextView(this);
            third.setTextColor(getResources().getColor(R.color.black));
            third.setLayoutParams(layoutParams);

            if(result.equalsIgnoreCase("studentteacher")){
                third.setText(timelogArray.get(i).getLocationName());
            }else{
                third.setText(timelogArray.get(i).getUsername());
            }

            row.addView(time);
            row.addView(entryType);
            row.addView(third);
            timelogLayout.addView(row);
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
