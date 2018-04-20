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
import android.widget.RadioGroup;

import com.app.rlts.R;
import com.app.rlts.activity.TimelogActivity;

public class TimelogFragment extends Fragment{

    View inflateView;
    RadioGroup radioStatus;

    Fragment fragment = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        inflateView = inflater.inflate(R.layout.fragment_timelog, container, false);

        radioStatus = (RadioGroup) inflateView.findViewById(R.id.radio_timelog);

        radioStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.timelog_studentteacher:
                        fragment = new StudentTeacherFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.timelog_classroom:
                        fragment = new ClassroomFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.timelog_facility:
                        fragment = new FacilityFragment();
                        loadFragment(fragment);
                        break;
                }

            }
        });

        // loading default fragment
        loadFragment(new StudentTeacherFragment());

        Button searchButton = (Button) inflateView.findViewById(R.id.timelog_search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TimelogActivity.class);
                startActivity(i);
            }
        });

        return inflateView;
    }

    private void loadFragment(Fragment fragment){
        if(fragment != null){
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentholder_radio, fragment)
                    .commit();
        }
    }
}