package com.example.fingerjam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class CreateRoomActivity extends AppCompatActivity {

    private TextView randomCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        initializeFields();
        randomCode.setText(String.valueOf((long) Math.floor(Math.random() * 9_000_000_00L) + 1_000_000_00L));

    }

    private void initializeFields() {
        randomCode = (TextView) findViewById(R.id.randomCode);
    }

}
