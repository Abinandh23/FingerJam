package com.example.fingerjam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class CreateOrJoinRoomActivity extends AppCompatActivity {
    private Button createRoom;
    private Button joinRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join_room);
        initializeFields();
        clickCreateRoom();
    }

    private void initializeFields() {
        createRoom = (Button) findViewById(R.id.createRoom);
        joinRoom = (Button) findViewById(R.id.joinRoom);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void clickCreateRoom() {
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createRoomIntent = new Intent(CreateOrJoinRoomActivity.this, CreateRoomActivity.class);
                startActivity(createRoomIntent);
            }
        });
    }
}
