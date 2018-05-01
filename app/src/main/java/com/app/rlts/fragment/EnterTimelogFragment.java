package com.app.rlts.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.app.rlts.R;
import com.app.rlts.activity.LocationTimelogActivity;
import com.app.rlts.entity.Timelog;

import java.util.ArrayList;

public class EnterTimelogFragment extends android.support.v4.app.Fragment {

    View inflateView;
    ArrayList<Timelog> timelogArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        inflateView = inflater.inflate(R.layout.fragment_enter_timelog, container, false);

        LocationTimelogActivity activity = (LocationTimelogActivity) getActivity();
        timelogArray = activity.getTimelogData();

//        Bundle bundle = getArguments();
//        timelogArray = (ArrayList<Timelog>) bundle.getSerializable("timelogArray");
//
        TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        rowLayoutParams.setMargins(50, 1, 50, 1);

        TextView enter_name_header = (TextView) inflateView.findViewById(R.id.enter_timelog_name);
        enter_name_header.setText(timelogArray.get(0).getLocationName());

        TextView date = (TextView) inflateView.findViewById(R.id.enter_timelog_date);
        date.setText(timelogArray.get(0).getDate());

        TableLayout enter_table = (TableLayout) inflateView.findViewById(R.id.enter_timelog_table);

        for (int i = 0; i < timelogArray.size(); i++) {

            if (timelogArray.get(i).getEntryType().equalsIgnoreCase("enter")) {

                TableRow row = new TableRow(getActivity());
                row.setLayoutParams(rowLayoutParams);
                row.setBackgroundResource(R.drawable.border);

                TextView time = new TextView(getActivity());
                time.setText(timelogArray.get(i).getTime());
                time.setTextColor(getResources().getColor(R.color.black));
                time.setLayoutParams(rowLayoutParams);

                TextView name = new TextView(getActivity());
                name.setText(timelogArray.get(i).getUsername());
                name.setTextColor(getResources().getColor(R.color.black));
                name.setLayoutParams(rowLayoutParams);

                row.addView(time);
                row.addView(name);
                enter_table.addView(row);

            }
        }

        return inflateView;
    }
}
