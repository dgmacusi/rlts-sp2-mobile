package com.app.rlts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimelogFragment extends android.support.v4.app.Fragment {
    public static final String ARG_TITLE = "arg_title";
    private TextView textView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timelog, container, false);

        textView = (TextView) rootView.findViewById(R.id.text_activetab);

        String title = getArguments().getString(ARG_TITLE, "");
        textView.setText(title);
        textView.setTextSize(20);

        return rootView;
    }
}
