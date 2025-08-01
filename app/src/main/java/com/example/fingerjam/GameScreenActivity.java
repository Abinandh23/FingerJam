package com.example.fingerjam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class GameScreenActivity extends AppCompatActivity {

    private TextView touchCount;
    private TextView showTimer, startText;
    private LinearLayout touchLayout;
    private int counter = 0;
    private String timerString = "";
    private int secondsTimer;
    private int noOfTouches;
    private boolean isNoOfTouchesBased;
    private boolean isTimerBased;
    private boolean isRunning = false;

    private long startTime = 0L;
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long updateTime = 0L;
    Handler customHandler = new Handler();

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwap + timeInMillies;
            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs %= 60;
            int milliseconds = (int) (updateTime % 100);
            showTimer.setText("" + String.format("%02dm %02ds %2d", mins, secs, milliseconds));
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        initializeFields();
        setIntentExtras();
        if(isTimerBased) {
             timerString = ""+String.format("%02dm %02ds",
                TimeUnit.MILLISECONDS.toMinutes(secondsTimer * 1000),
                TimeUnit.MILLISECONDS.toSeconds(secondsTimer * 1000) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(secondsTimer * 1000)));
            showTimer.setText(timerString);

            checkAndShowTouchCount();
        }

        if(isNoOfTouchesBased) {
            showTimer.setText("00m 00s 00");
            checkAndShowTouchCount();
        }

    }

    private void setIntentExtras() {
        isNoOfTouchesBased = (boolean) getIntent().getExtras().get("isNoOfTouchesBased");
        isTimerBased = (boolean) getIntent().getExtras().get("isTimerBased");
        if(isNoOfTouchesBased && !isTimerBased) {
            noOfTouches = Integer.valueOf(getIntent().getExtras().get("noOfTouches").toString());
        }

        if(!isNoOfTouchesBased && isTimerBased) {
            secondsTimer = Integer.valueOf(getIntent().getExtras().get("noOfSeconds").toString());
        }
    }


    private void initializeFields() {
        touchCount = (TextView) findViewById(R.id.showCount);
        touchLayout = (LinearLayout) findViewById(R.id.touchLayout);
        showTimer = (TextView) findViewById(R.id.showTimer);
        startText = (TextView) findViewById(R.id.startText);
    }

    private void checkAndShowTouchCount() {
        touchLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startText.setText("");
                if(counter == 0 && isTimerBased && !isNoOfTouchesBased) {

                    setCountDownTimer();
                }
                if(isNoOfTouchesBased && !isTimerBased) {
                    setCountUpTimer();


                }
                counter += event.getPointerCount();
                touchCount.setText(counter + "");
                return false;
            }
        });
    }


    private void setCountDownTimer() {

        new CountDownTimer(secondsTimer * 1000 + 1000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                showTimer.setText(""+String.format("%02dm %02ds",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                showTimer.setText("⏰ TIME'S UP!");
                touchLayout.setOnTouchListener(null);
                setVibrator();
                startText.setTextSize(25);
                startText.setText("🎉 " + counter + " touches in " + timerString + " 🎉");
                startText.setTextColor(getResources().getColor(R.color.success_green));
                setAlphaToTextViewAfterFinish();
            }
        }.start();

    }

    private void setCountUpTimer() {
        if(!isRunning) {
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            isRunning = true;
        }


        if (counter + 1 == noOfTouches) {
            touchLayout.setOnTouchListener(null);
            timeSwap += timeInMillies;
            customHandler.removeCallbacks(updateTimerThread);
            isRunning = false;
            startText.setTextSize(25);
            startText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            startText.setText("🏆 " + noOfTouches +" touches reached in " + showTimer.getText() + " 🏆");
            startText.setTextColor(getResources().getColor(R.color.success_green));
            setVibrator();
            setAlphaToTextViewAfterFinish();
        }

    }

    private void setVibrator() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private void setAlphaToTextViewAfterFinish() {
        touchCount.setAlpha(1);
        showTimer.setAlpha(1);
    }

}
