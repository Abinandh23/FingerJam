package com.example.fingerjam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class GameScreenActivity extends AppCompatActivity {

    private TextView touchCount;
    private TextView showTimer, startText;
    private TextView instructionText;
    private LinearLayout touchLayout;
    private Button replayButton;
    private int counter = 0;
    private String timerString = "";
    private int secondsTimer;
    private int noOfTouches;
    private boolean isNoOfTouchesBased;
    private boolean isTimerBased;
    private boolean isRunning = false;
    private boolean gameEnded = false;
    private boolean timeChallengeEnded = false;
    private ThemeManager themeManager;

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
            showTimer.setText(String.format("%02dm %02ds %02dms", mins, secs, milliseconds));
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        themeManager = ThemeManager.getInstance(this);
        initializeFields();
        setIntentExtras();
        setupReplayButton();
        applyCurrentTheme();
        
        if(isTimerBased) {
             timerString = ""+String.format("%02dm %02ds",
                TimeUnit.MILLISECONDS.toMinutes(secondsTimer * 1000),
                TimeUnit.MILLISECONDS.toSeconds(secondsTimer * 1000) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(secondsTimer * 1000)));
            showTimer.setText(timerString);
            // Set larger font size for time challenge since it only shows "00m 00s"
            showTimer.setTextSize(18);

            checkAndShowTouchCount();
        }

        if(isNoOfTouchesBased) {
            showTimer.setText("00m 00s 00ms");
            // Keep smaller font size for touch challenge since it shows "00m 00s 00ms"
            showTimer.setTextSize(14);
            checkAndShowTouchCount();
        }

    }

    private void applyCurrentTheme() {
        // Apply theme to game elements
        themeManager.applyThemeToView(touchLayout);
        themeManager.applyThemeToView(replayButton);
        
        // Apply text colors
        themeManager.applyThemeToTextView(touchCount, true);
        themeManager.applyThemeToTextView(showTimer, true);
        themeManager.applyThemeToTextView(startText, true);
        themeManager.applyThemeToTextView(instructionText, false);
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
        touchCount = findViewById(R.id.showCount);
        touchLayout = findViewById(R.id.touchLayout);
        showTimer = findViewById(R.id.showTimer);
        startText = findViewById(R.id.startText);
        replayButton = findViewById(R.id.replayButton);
        instructionText = findViewById(R.id.instructionText);
    }

    private void setupReplayButton() {
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to practice menu
                Intent intent = new Intent(GameScreenActivity.this, PracticeMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkAndShowTouchCount() {
        touchLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Don't count touches if time challenge has ended
                if (timeChallengeEnded) {
                    return false;
                }
                
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
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                
                showTimer.setText(String.format(java.util.Locale.getDefault(), "%02dm %02ds", minutes, seconds));
                
                // Check if timer reached 00m 00s and end the game immediately
                if (minutes == 0 && seconds == 0) {
                    onFinish();
                }
            }

            public void onFinish() {
                timeChallengeEnded = true; // Set flag to prevent further touches
                showTimer.setText("⏰ TIME'S UP!");
                touchLayout.setOnTouchListener(null);
                setVibrator();
                startText.setTextSize(25);
                startText.setText("🎉 " + counter + " touches in\n     " + timerString + " 🎉");
                startText.setTextColor(getResources().getColor(R.color.success_green));
                
                // Hide the instruction text
                instructionText.setVisibility(View.GONE);
                
                // Highlight the number of touches with a different color and larger font
                String resultText = "🎉 " + counter + " touches in\n     " + timerString + " 🎉";
                startText.setText(resultText);
                
                // Create a SpannableString to highlight the touch count
                android.text.SpannableString spannableString = new android.text.SpannableString(resultText);
                int touchCountStart = resultText.indexOf(String.valueOf(counter));
                int touchCountEnd = touchCountStart + String.valueOf(counter).length();
                
                // Apply highlight color and larger font size to the touch count
                spannableString.setSpan(
                    new android.text.style.ForegroundColorSpan(getResources().getColor(R.color.neon_pink)),
                    touchCountStart,
                    touchCountEnd,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                
                // Apply larger font size to the touch count
                spannableString.setSpan(
                    new android.text.style.RelativeSizeSpan(1.5f),
                    touchCountStart,
                    touchCountEnd,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                
                startText.setText(spannableString);
                setAlphaToTextViewAfterFinish();
                showReplayButton();
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
            
            // Hide the instruction text
            instructionText.setVisibility(View.GONE);
            
            // Use the exact time that's displayed on the timer (same format now)
            String currentTimerText = showTimer.getText().toString();
            String resultText = "🏆 " + noOfTouches +" touches reached in " + currentTimerText + " 🏆";
            startText.setText(resultText);
            
            // Create a SpannableString to highlight the time
            android.text.SpannableString spannableString = new android.text.SpannableString(resultText);
            int timeStart = resultText.indexOf(currentTimerText);
            int timeEnd = timeStart + currentTimerText.length();
            
            // Apply highlight color and larger font size to the time
            spannableString.setSpan(
                new android.text.style.ForegroundColorSpan(getResources().getColor(R.color.neon_pink)),
                timeStart,
                timeEnd,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            
            // Apply larger font size to the time
            spannableString.setSpan(
                new android.text.style.RelativeSizeSpan(1.2f),
                timeStart,
                timeEnd,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            
            startText.setText(spannableString);
            startText.setTextColor(getResources().getColor(R.color.success_green));
            setVibrator();
            setAlphaToTextViewAfterFinish();
            showReplayButton();
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

    private void showReplayButton() {
        // Add a 5-second delay before showing the replay button
        customHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                replayButton.setVisibility(View.VISIBLE);
            }
        }, 5000); // 5000 milliseconds = 5 seconds
    }

    @Override
    public void onBackPressed() {
        // Navigate back to practice menu when back button is pressed
        Intent intent = new Intent(GameScreenActivity.this, PracticeMenuActivity.class);
        startActivity(intent);
        finish();
    }

}
