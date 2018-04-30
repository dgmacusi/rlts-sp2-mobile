package com.app.rlts.activity;

import android.content.Intent;
import android.graphics.Typeface;
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

        TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        rowLayoutParams.setMargins(50, 1, 50, 1);

        TextView name = (TextView) findViewById(R.id.timelog_name);

        TextView date = (TextView) findViewById(R.id.timelog_date);
        date.setText(timelogArray.get(0).getDate());

        TableLayout table1 = (TableLayout) findViewById(R.id.timelog_table);

        TextView header1 = (TextView) findViewById(R.id.header1);
        TextView header2 = (TextView) findViewById(R.id.header2);

        LinearLayout.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        layoutParams.setMargins(50, 1, 50, 1);

        if (result.equalsIgnoreCase("studentteacher")) {
            name.setText(timelogArray.get(0).getUsername());
            header1.setText(getString(R.string.entry_type));
            header2.setText(getString(R.string.location));
        } else {

            TableRow enter_header = (TableRow) findViewById(R.id.enter_header);
            enter_header.setBackgroundResource(R.drawable.border);

            TextView enter = (TextView) findViewById(R.id.enter);
            enter.setText(R.string.entry_type_enter);

            name.setText(timelogArray.get(0).getLocationName());
            header1.setText(R.string.name);
        }

        for (int i = 0; i < timelogArray.size(); i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(rowLayoutParams);
            row.setBackgroundResource(R.drawable.border);

            TextView time = new TextView(this);
            time.setText(timelogArray.get(i).getTime());
            time.setTextColor(getResources().getColor(R.color.black));
            time.setLayoutParams(rowLayoutParams);

            TextView third = new TextView(this);
            third.setTextColor(getResources().getColor(R.color.black));
            third.setLayoutParams(rowLayoutParams);

            if (result.equalsIgnoreCase("studentteacher")) {

                TextView entryType = new TextView(this);
                entryType.setText(timelogArray.get(i).getEntryType());
                entryType.setTextColor(getResources().getColor(R.color.black));
                entryType.setLayoutParams(rowLayoutParams);
                row.addView(entryType);

                third.setText(timelogArray.get(i).getLocationName());
            } else {
                third.setText(timelogArray.get(i).getUsername());
            }

            row.addView(time);
            row.addView(third);
            table1.addView(row);
        }

        if (!(result.equalsIgnoreCase("studentteacher"))) {

            TableLayout table2 = (TableLayout) findViewById(R.id.timelog_table_exit);

            TableRow exit_header = (TableRow) findViewById(R.id.exit_header);
            exit_header.setBackgroundResource(R.drawable.border);

            TextView exit = (TextView) findViewById(R.id.exit);
            exit.setText(R.string.entry_type_exit);

            TableRow row_header = new TableRow(this);
            row_header.setLayoutParams(rowLayoutParams);
            row_header.setBackgroundResource(R.drawable.border);

            TextView time = new TextView(this);
            time.setText(R.string.time);
            time.setTextColor(getResources().getColor(R.color.black));
            time.setTypeface(null, Typeface.BOLD);
            time.setLayoutParams(rowLayoutParams);

            TextView header3 = new TextView(this);
            header3.setText(R.string.name);
            header3.setTextColor(getResources().getColor(R.color.black));
            header3.setTypeface(null, Typeface.BOLD);
            header3.setLayoutParams(rowLayoutParams);

            row_header.addView(time);
            row_header.addView(header3);
            table2.addView(row_header);

            for (int i = 0; i < timelogArray.size(); i++) {

                TableRow row = new TableRow(this);
                row.setLayoutParams(rowLayoutParams);
                row.setBackgroundResource(R.drawable.border);

                TextView exit_time = new TextView(this);
                exit_time.setText(timelogArray.get(i).getTime());
                exit_time.setTextColor(getResources().getColor(R.color.black));
                exit_time.setLayoutParams(rowLayoutParams);

                TextView third = new TextView(this);
                third.setText(timelogArray.get(i).getUsername());
                third.setTextColor(getResources().getColor(R.color.black));
                third.setLayoutParams(rowLayoutParams);

                row.addView(exit_time);
                row.addView(third);
                table2.addView(row);
            }
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
