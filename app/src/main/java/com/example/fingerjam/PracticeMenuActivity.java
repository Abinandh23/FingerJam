package com.example.fingerjam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PracticeMenuActivity extends AppCompatActivity {

    private EditText touchesText;
    private Button touchesButton;
    private EditText secondsText;
    private Button secondsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_menu);

        initializeFields();

        //emptyTouchesField();

        //emptySecondsField();



        startTouchesGame();

        startSecondsGame();
    }


    private void initializeFields() {
        touchesText = (EditText) findViewById(R.id.touchesText);
        touchesButton = (Button) findViewById(R.id.touchesStartButton);
        secondsText = (EditText) findViewById(R.id.secondsText);
        secondsButton = (Button) findViewById(R.id.secondsStartButton);
    }

    private void emptySecondsField() {
        /*touchesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondsText.setText("");
            }});

        secondsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchesText.setText("");
            }});*/
        touchesText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    secondsText.setText("");
                    return true;
                }

                return false;
            }
        });

    }

    private void emptyTouchesField() {


        secondsText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    touchesText.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    private void startTouchesGame() {
        touchesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(touchesText.getText().toString())) {
                    Toast.makeText(PracticeMenuActivity.this, "Please enter the No. of touches to start", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent touchesIntent = new Intent(PracticeMenuActivity.this, GameScreenActivity.class);
                    touchesIntent.putExtra("noOfTouches", touchesText.getText().toString());
                    touchesIntent.putExtra("isNoOfTouchesBased", true);
                    touchesIntent.putExtra("isTimerBased", false);
                    startActivity(touchesIntent);
                }
                secondsText.setText("");
            }
        });

    }

    private void startSecondsGame() {
        secondsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(secondsText.getText().toString())) {
                    Toast.makeText(PracticeMenuActivity.this, "Please enter the No. of seconds to start", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent secondsIntent = new Intent(PracticeMenuActivity.this, GameScreenActivity.class);
                    secondsIntent.putExtra("noOfSeconds", secondsText.getText());
                    secondsIntent.putExtra("isNoOfTouchesBased", false);
                    secondsIntent.putExtra("isTimerBased", true);
                    startActivity(secondsIntent);
                }
                touchesText.setText("");
            }
        });
    }
}
