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
import com.app.rlts.manager.SessionManager;

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

        TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        rowLayoutParams.setMargins(50, 1, 50, 1);

        TextView name = (TextView) findViewById(R.id.timelog_name);
        name.setText(timelogArray.get(0).getUsername());

        TextView date = (TextView) findViewById(R.id.timelog_date);
        date.setText(timelogArray.get(0).getDate());

        TableLayout person_table = (TableLayout) findViewById(R.id.timelog_table);

        for (int i = 0; i < timelogArray.size(); i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(rowLayoutParams);
            row.setBackgroundResource(R.drawable.border);

            TextView time = new TextView(this);
            time.setText(timelogArray.get(i).getTime());
            time.setTextColor(getResources().getColor(R.color.black));
            time.setLayoutParams(rowLayoutParams);

            TextView entryType = new TextView(this);
            entryType.setText(timelogArray.get(i).getEntryType());
            entryType.setTextColor(getResources().getColor(R.color.black));
            entryType.setLayoutParams(rowLayoutParams);

            TextView location = new TextView(this);
            location.setText(timelogArray.get(i).getLocationName());
            location.setTextColor(getResources().getColor(R.color.black));
            location.setLayoutParams(rowLayoutParams);

            row.addView(time);
            row.addView(entryType);
            row.addView(location);
            person_table.addView(row);
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
