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
import android.widget.TextView;

import com.app.rlts.R;
import com.app.rlts.entity.Timelog;
import com.app.rlts.interfaces.AsyncTimelogResponse;
import com.app.rlts.task.AsyncGetStudentTeacherTimelogTask;

import java.util.ArrayList;

public class StudentTeacherFragment extends Fragment implements AsyncTimelogResponse {

    View inflateView;
    Button searchButton;

    ArrayList<Timelog> timelogArray = new ArrayList<>();

    EditText studentteacher_date;
    EditText studentteacher_no;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflateView = inflater.inflate(R.layout.fragment_studentteacher, container, false);

        studentteacher_date = (EditText) inflateView.findViewById(R.id.studentteacher_date);
        studentteacher_no = (EditText) inflateView.findViewById(R.id.studentteacher_number);

        searchButton = (Button) inflateView.findViewById(R.id.studentteacher_search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final Intent i = new Intent(getActivity(), TimelogActivity.class);
//                i.putExtra("fragment", "studentteacher");
//                startActivity(i);

                String date = studentteacher_date.getText().toString();
                String number = studentteacher_no.getText().toString();

                new AsyncGetStudentTeacherTimelogTask(StudentTeacherFragment.this).execute(date, number);
            }
        });

        return inflateView;
    }

    @Override
    public void retrieveTimelog(ArrayList<Timelog> tArray) {

        try {
            for (int i = 0; i < tArray.size(); i++) {
                this.timelogArray.add(tArray.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView checkview = (TextView) inflateView.findViewById(R.id.checkview);
        checkview.setText(tArray.get(0).getDate());

//        Intent i = new Intent(getActivity(), TimelogActivity.class);
//        i.putExtra("fragment", "studentteacher");
//        i.putExtra("timelogArray", timelogArray);
//        startActivity(i);
    }
}
