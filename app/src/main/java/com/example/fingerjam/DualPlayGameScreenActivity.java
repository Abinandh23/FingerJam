package com.example.fingerjam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DualPlayGameScreenActivity extends AppCompatActivity {

    private LinearLayout topLayout;
    private TextView centerLine;
    private LinearLayout bottomLayout;
    private int screenHeight;
    private int screenWidth;
    private int initialTopHeight;
    private int initialBottomHeight;
    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dual_play_game_screen);

        initializeFields();
        checkOrientation();
        setupInitialLayout();
        touchingTopLayout();
        touchingBottonLayout();
    }

    private void initializeFields() {
        topLayout = (LinearLayout) findViewById(R.id.topLayout);
        centerLine = (TextView) findViewById(R.id.blackLine);
        bottomLayout = (LinearLayout) findViewById(R.id.bottomLayout);
    }

    private void checkOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void setupInitialLayout() {
        // Get screen dimensions
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        
        if (isLandscape) {
            // For landscape, divide width instead of height
            initialTopHeight = screenWidth / 2;
            initialBottomHeight = screenWidth / 2;
            
            topLayout.getLayoutParams().width = initialTopHeight;
            bottomLayout.getLayoutParams().width = initialBottomHeight;
        } else {
            // For portrait, divide height
            initialTopHeight = screenHeight / 2;
            initialBottomHeight = screenHeight / 2;
            
            topLayout.getLayoutParams().height = initialTopHeight;
            bottomLayout.getLayoutParams().height = initialBottomHeight;
        }
        
        topLayout.requestLayout();
        bottomLayout.requestLayout();
    }

    private void touchingTopLayout() {
        topLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isLandscape) {
                    handleLandscapeTouch(true);
                } else {
                    handlePortraitTouch(true);
                }
                return false;
            }
        });
    }

    private void touchingBottonLayout() {
        bottomLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isLandscape) {
                    handleLandscapeTouch(false);
                } else {
                    handlePortraitTouch(false);
                }
                return false;
            }
        });
    }

    private void handlePortraitTouch(boolean isTopPlayer) {
        int bottomLayoutHeight = bottomLayout.getMeasuredHeight();
        int topLayoutHeight = topLayout.getMeasuredHeight();
        
        if (isTopPlayer) {
            // Top player wins - increase top, decrease bottom
            int newBottomHeight = Math.max(50, bottomLayoutHeight - 30);
            int newTopHeight = screenHeight - newBottomHeight - centerLine.getMeasuredHeight();
            
            bottomLayout.getLayoutParams().height = newBottomHeight;
            topLayout.getLayoutParams().height = newTopHeight;
        } else {
            // Bottom player wins - increase bottom, decrease top
            int newTopHeight = Math.max(50, topLayoutHeight - 30);
            int newBottomHeight = screenHeight - newTopHeight - centerLine.getMeasuredHeight();
            
            topLayout.getLayoutParams().height = newTopHeight;
            bottomLayout.getLayoutParams().height = newBottomHeight;
        }
        
        bottomLayout.requestLayout();
        topLayout.requestLayout();
    }

    private void handleLandscapeTouch(boolean isLeftPlayer) {
        int leftLayoutWidth = topLayout.getMeasuredWidth();
        int rightLayoutWidth = bottomLayout.getMeasuredWidth();
        
        if (isLeftPlayer) {
            // Left player wins - increase left, decrease right
            int newRightWidth = Math.max(50, rightLayoutWidth - 30);
            int newLeftWidth = screenWidth - newRightWidth - centerLine.getMeasuredWidth();
            
            bottomLayout.getLayoutParams().width = newRightWidth;
            topLayout.getLayoutParams().width = newLeftWidth;
        } else {
            // Right player wins - increase right, decrease left
            int newLeftWidth = Math.max(50, leftLayoutWidth - 30);
            int newRightWidth = screenWidth - newLeftWidth - centerLine.getMeasuredWidth();
            
            topLayout.getLayoutParams().width = newLeftWidth;
            bottomLayout.getLayoutParams().width = newRightWidth;
        }
        
        bottomLayout.requestLayout();
        topLayout.requestLayout();
    }
}
