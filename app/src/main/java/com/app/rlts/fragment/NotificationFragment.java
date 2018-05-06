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
import android.widget.LinearLayout;

import com.app.rlts.R;
import com.app.rlts.entity.Beacon;
import com.app.rlts.entity.Notification;
import com.app.rlts.interfaces.AsyncResponse;
import com.app.rlts.task.AsyncGetBeaconsTask;
import com.app.rlts.task.AsyncSendNotificationTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NotificationFragment extends Fragment implements AsyncResponse{

    View inflateView;
    Button sendButton;

    Calendar calendar;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;

    ArrayList<Beacon> beaconArray = new ArrayList<>();

    EditText notif_title;
    EditText notif_sendTo;
    EditText notif_body;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        inflateView = inflater.inflate(R.layout.fragment_notification, container, false);

        LinearLayout fragment_notif_layout = (LinearLayout) inflateView.findViewById(R.id.fragment_notif_layout);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");

        notif_title = (EditText) inflateView.findViewById(R.id.notif_title);
        notif_sendTo = (EditText) inflateView.findViewById(R.id.notif_sendto);
        notif_body = (EditText) inflateView.findViewById(R.id.notif_body);

        new AsyncGetBeaconsTask(this).execute();

        /*TextView checkboxTextview = (TextView) inflateView.findViewById(R.id.checkbox_textview);

        boolean[] checkSelected = new boolean[beaconArray.size()];
        for(int i = 0; i < checkSelected.length; i++){
            checkSelected[i] = false;
        }

        LayoutInflater popupInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout popupLayout = (LinearLayout) popupInflater.inflate(R.layout.send_to_list_view,
                (ViewGroup) inflateView.findViewById(R.id.send_to_popup));

        final PopupWindow popupWindow = new PopupWindow(popupLayout,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // backgroung can not be null if we want to touch the event to be active outside of the pop-up window
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        popupWindow.setTouchable(true);

        // inform pop-up of touch events outside its window
        popupWindow.setOutsideTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        popupWindow.setContentView(popupLayout);
        popupWindow.showAsDropDown(fragment_notif_layout);

        ListView listView = (ListView) popupLayout.findViewById(R.id.send_to_dropdown);
        DropDownListAdapter manager = new DropDownListAdapter(this, beaconArray, checkboxTextview);
        listView.setAdapter(manager);*/

        sendButton = (Button) inflateView.findViewById(R.id.notif_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                String date = dateFormat.format(calendar.getTime());
                String time = dateFormat.format(calendar.getTime());

                String title = notif_title.getText().toString();
                String sendTo = notif_sendTo.getText().toString();
                String body = notif_body.getText().toString();

                Notification notification = new Notification(date, time, title, sendTo, body);
                new AsyncSendNotificationTask(notification).execute();
            }
        });

        return inflateView;
    }

    @Override
    public void retrieveBeacons(ArrayList<Beacon> bList) {


        try{
            beaconArray.clear();
            for (int i = 0; i < bList.size(); i++) {
                this.beaconArray.add(bList.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }
    }
}
