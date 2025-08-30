package com.example.fingerjam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.app.AlertDialog;

public class PracticeMenuActivity extends AppCompatActivity {

    private EditText touchesText;
    private Button touchesButton;
    private EditText secondsText;
    private Button secondsButton;
    private Button themeButton;
    private LinearLayout mainLayout;
    private TextView titleText;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_menu);

        themeManager = ThemeManager.getInstance(this);
        initializeFields();
        setupInputHandling();
        applyCurrentTheme();

        startTouchesGame();
        startSecondsGame();
        setupThemeButton();
    }

    private void initializeFields() {
        touchesText = (EditText) findViewById(R.id.touchesText);
        touchesButton = (Button) findViewById(R.id.touchesStartButton);
        secondsText = (EditText) findViewById(R.id.secondsText);
        secondsButton = (Button) findViewById(R.id.secondsStartButton);
        themeButton = (Button) findViewById(R.id.themeButton);
        mainLayout = findViewById(R.id.mainLayout);
        titleText = (TextView) findViewById(R.id.titleText);
        
        // Add multiplayer button
        Button multiplayerButton = findViewById(R.id.multiplayerButton);
        if (multiplayerButton != null) {
            multiplayerButton.setOnClickListener(v -> {
                Intent intent = new Intent(PracticeMenuActivity.this, MultiplayerMenuActivity.class);
                startActivity(intent);
            });
        }

        // Set input filters for touchesText
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(7); // Allow up to 7 digits
        touchesText.setFilters(filters);

        // Set input filters for secondsText
        InputFilter[] secondsFilters = new InputFilter[1];
        secondsFilters[0] = new InputFilter.LengthFilter(6); // Allow up to 6 digits
        secondsText.setFilters(secondsFilters);
    }

    private void setupThemeButton() {
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showThemePickerDialog();
            }
        });
    }

    private void showThemePickerDialog() {
        String[] themes = {"Default", "Dark", "Neon", "Minimalist"};
        int currentTheme = themeManager.getCurrentTheme();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Theme")
               .setSingleChoiceItems(themes, currentTheme, (dialog, which) -> {
                   themeManager.setTheme(which);
                   applyCurrentTheme();
                   dialog.dismiss();
                   Toast.makeText(PracticeMenuActivity.this, 
                       "Theme changed to " + themes[which], Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void applyCurrentTheme() {
        // Apply background
        mainLayout.setBackgroundResource(themeManager.getBackgroundDrawable());
        
        // Apply theme to all UI elements
        themeManager.applyThemeToView(touchesText);
        themeManager.applyThemeToView(secondsText);
        themeManager.applyThemeToView(touchesButton);
        themeManager.applyThemeToView(secondsButton);
        themeManager.applyThemeToView(themeButton);
        
        // Apply text colors
        themeManager.applyThemeToTextView(touchesText, true);
        themeManager.applyThemeToTextView(secondsText, true);
        themeManager.applyThemeToTextView(titleText, true);
        
        // Update theme button text
        themeButton.setText("🎨 " + themeManager.getThemeName());
    }

    private void setupInputHandling() {
        // Handle Enter key press for touches input
        touchesText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, android.view.KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Hide the keyboard and clear focus
                    android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(touchesText.getWindowToken(), 0);
                    touchesText.clearFocus();
                    return true;
                }
                return false;
            }
        });

        // Handle Enter key press for seconds input
        secondsText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, android.view.KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Hide the keyboard and clear focus
                    android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(secondsText.getWindowToken(), 0);
                    secondsText.clearFocus();
                    return true;
                }
                return false;
            }
        });

        // Clear seconds field when touches field is focused
        touchesText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    secondsText.setText("");
                }
            }
        });

        // Clear touches field when seconds field is focused
        secondsText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    touchesText.setText("");
                }
            }
        });
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
                    int touches = Integer.parseInt(touchesText.getText().toString());
                    if (touches > 1000000) {
                        Toast.makeText(PracticeMenuActivity.this, "Maximum touches allowed is 1,000,000", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
                    int seconds = Integer.parseInt(secondsText.getText().toString());
                    if (seconds > 100000) {
                        Toast.makeText(PracticeMenuActivity.this, "Maximum seconds allowed is 100,000", Toast.LENGTH_SHORT).show();
                        return;
                    }
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

    @Override
    public void onBackPressed() {
        // Navigate back to landing screen when back button is pressed
        Intent intent = new Intent(PracticeMenuActivity.this, LandingActivity.class);
        startActivity(intent);
        finish();
    }
}
