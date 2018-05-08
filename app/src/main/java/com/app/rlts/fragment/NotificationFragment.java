package com.app.rlts.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.rlts.R;
import com.app.rlts.activity.HomeActivity;
import com.app.rlts.entity.Beacon;
import com.app.rlts.entity.StateVO;
import com.app.rlts.entity.WebNotification;
import com.app.rlts.interfaces.AsyncResponse;
import com.app.rlts.manager.SessionManager;
import com.app.rlts.manager.SpinnerAdapter;
import com.app.rlts.task.AsyncGetBeaconsTask;
import com.app.rlts.task.AsyncSendNotificationTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class NotificationFragment extends Fragment implements AsyncResponse{

    View inflateView;
    SessionManager session;
    Button sendButton;

    Calendar calendar;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;

    ArrayList<Beacon> beaconArray = new ArrayList<>();

    EditText notif_title;
    EditText notif_sendTo;
    EditText notif_body;

    Spinner spinner;
    SpinnerAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        inflateView = inflater.inflate(R.layout.fragment_notification, container, false);

        session = new SessionManager(getActivity().getApplicationContext());

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");

        spinner = (Spinner) inflateView.findViewById(R.id.spinner_location);

        new AsyncGetBeaconsTask(this).execute();

        notif_title = (EditText) inflateView.findViewById(R.id.notif_title);
        notif_body = (EditText) inflateView.findViewById(R.id.notif_body);

        sendButton = (Button) inflateView.findViewById(R.id.notif_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HomeActivity.tryvar = "hello";

                calendar = Calendar.getInstance();
                String date = dateFormat.format(calendar.getTime());
                String time = dateFormat.format(calendar.getTime());

                String title = notif_title.getText().toString();
                ArrayList<String> locations = myAdapter.getListState();
                String body = notif_body.getText().toString();

                HashMap<String, String> user = session.getUserDetails();
                String username = user.get(SessionManager.KEY_NAME);

                TextView check = (TextView) inflateView.findViewById(R.id.notif_sendto);
                check.setText(HomeActivity.tryvar);

                WebNotification notification = new WebNotification(date, time, title, locations, body, username);
                new AsyncSendNotificationTask(notification).execute();

                new AsyncGetBeaconsTask(NotificationFragment.this).execute();
            }
        });

        return inflateView;
    }

    @Override
    public void retrieveBeacons(ArrayList<Beacon> bList) {

        try{
            beaconArray.clear();
            for (int i = 0; i < bList.size(); i++) {
                if(!(bList.get(i).getLocationName().equalsIgnoreCase("NA"))){
                    this.beaconArray.add(bList.get(i));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }

        ArrayList<StateVO> listVOs = new ArrayList<>();

        StateVO header = new StateVO();
        header.setLocation(getString(R.string.send_to_location));
        header.setSelected(false);
        listVOs.add(header);

        for (int i = 0; i < beaconArray.size(); i++) {
            StateVO stateVO = new StateVO();
            stateVO.setLocation(beaconArray.get(i).getLocationName());
            stateVO.setId(beaconArray.get(i).getBeaconId());
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }

        myAdapter = new SpinnerAdapter(getActivity(), 0, listVOs);
        spinner.setAdapter(myAdapter);
    }
}
