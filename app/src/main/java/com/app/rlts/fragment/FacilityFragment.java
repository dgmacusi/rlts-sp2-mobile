package com.app.rlts.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.app.rlts.R;
import com.app.rlts.activity.TimelogActivity;
import com.app.rlts.entity.Timelog;
import com.app.rlts.interfaces.AsyncTimelogResponse;
import com.app.rlts.task.AsyncGetFacilityTimelogTask;

import java.io.Serializable;
import java.util.ArrayList;

public class FacilityFragment extends Fragment implements AsyncTimelogResponse {

    View inflateView;
    Button searchButton;

    ArrayList<Timelog> facilityTimelog = new ArrayList<>();

    EditText facilty_date;
    EditText facility_roomname;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflateView = inflater.inflate(R.layout.fragment_facility, container, false);

        facilty_date = (EditText) inflateView.findViewById(R.id.facility_date);
        facility_roomname = (EditText) inflateView.findViewById(R.id.facility_roomname);

        searchButton = (Button) inflateView.findViewById(R.id.facility_search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = facilty_date.getText().toString();
                String room_name = facility_roomname.getText().toString();

                new AsyncGetFacilityTimelogTask(FacilityFragment.this).execute(date, room_name);

            }
        });

        return inflateView;
    }

    @Override
    public void retrieveTimelog(ArrayList<Timelog> tArray) {

        try{
            facilityTimelog.clear();
            for(int i = 0; i < tArray.size(); i++){
                this.facilityTimelog.add(tArray.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent i = new Intent(getActivity(), TimelogActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("timelogArray", (Serializable)facilityTimelog);

        i.putExtras(bundle);
        i.putExtra("fragment", "facility");
        startActivity(i);
    }
}
