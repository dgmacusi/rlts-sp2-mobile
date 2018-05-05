package com.app.rlts.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.app.rlts.R;
import com.app.rlts.activity.LocationTimelogActivity;
import com.app.rlts.entity.Timelog;
import com.app.rlts.interfaces.AsyncTimelogResponse;
import com.app.rlts.task.AsyncGetClassroomTimelogTask;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ClassroomFragment extends Fragment implements AsyncTimelogResponse{

    View inflateView;
    Button searchButton;

    ArrayList<Timelog> timelogArray = new ArrayList<>();

    EditText classroom_date;
    EditText classroom_gradelevel;
    EditText classroom_section;

    Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflateView = inflater.inflate(R.layout.fragment_classroom, container, false);

        classroom_date = (EditText) inflateView.findViewById(R.id.classroom_date);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        classroom_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(getActivity(), date,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        classroom_gradelevel = (EditText) inflateView.findViewById(R.id.classroom_gradelevel);
        classroom_section = (EditText) inflateView.findViewById(R.id.classroom_section);

        searchButton = (Button) inflateView.findViewById(R.id.classroom_search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = classroom_date.getText().toString();
                String grade_level = classroom_gradelevel.getText().toString();
                String section = classroom_section.getText().toString();

                new AsyncGetClassroomTimelogTask(ClassroomFragment.this).execute(date, grade_level, section);
            }
        });

        return inflateView;
    }

    private void updateLabel(){

        String format = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        classroom_date.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void retrieveTimelog(ArrayList<Timelog> tArray) {

        try {
            timelogArray.clear();
            for(int i = 0; i < tArray.size(); i++){
                this.timelogArray.add(tArray.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent i = new Intent(getActivity(), LocationTimelogActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("timelogArray", (Serializable)timelogArray);

        i.putExtras(bundle);
        startActivity(i);

    }
}
