package com.app.rlts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.app.rlts.R;

public class ImageActivity extends AppCompatActivity {

    //SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //session = new SessionManager(getApplicationContext());

        Button proceedButton = (Button) findViewById(R.id.proceed);

        //session.checkLogin();

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ImageActivity.this, LoginActivity.class);
                startActivity(i);
                //ImageActivity.this.finish();
            }
        });
    }
}
