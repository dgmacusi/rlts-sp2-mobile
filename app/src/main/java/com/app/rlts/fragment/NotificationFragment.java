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

import com.app.rlts.R;
import com.app.rlts.activity.TimelogActivity;

public class NotificationFragment extends Fragment {

    View inflateView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        inflateView = inflater.inflate(R.layout.fragment_notification, container, false);

        Button notifSend = (Button) getView().findViewById(R.id.notif_send);

        notifSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TimelogActivity.class);
                startActivity(i);
            }
        });

        return inflateView;
    }
}
