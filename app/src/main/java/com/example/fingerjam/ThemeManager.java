package com.example.fingerjam;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

public class ThemeManager {
    
    public static final String THEME_PREFS = "theme_prefs";
    public static final String CURRENT_THEME = "current_theme";
    
    public static final int THEME_DEFAULT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_NEON = 2;
    public static final int THEME_MINIMALIST = 3;
    
    private static ThemeManager instance;
    private Context context;
    private SharedPreferences prefs;
    private int currentTheme;
    
    private ThemeManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE);
        this.currentTheme = prefs.getInt(CURRENT_THEME, THEME_DEFAULT);
    }
    
    public static ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context);
        }
        return instance;
    }
    
    public int getCurrentTheme() {
        return currentTheme;
    }
    
    public void setTheme(int theme) {
        this.currentTheme = theme;
        prefs.edit().putInt(CURRENT_THEME, theme).apply();
    }
    
    public int getBackgroundDrawable() {
        switch (currentTheme) {
            case THEME_DARK:
                return R.drawable.dark_theme_background;
            case THEME_NEON:
                return R.drawable.neon_theme_background;
            case THEME_MINIMALIST:
                return R.drawable.minimalist_theme_background;
            default:
                return R.drawable.gradient_background;
        }
    }
    
    public int getGlowBorderDrawable() {
        switch (currentTheme) {
            case THEME_DARK:
                return R.drawable.dark_glow_border;
            case THEME_NEON:
                return R.drawable.neon_glow_border;
            case THEME_MINIMALIST:
                return R.drawable.minimalist_glow_border;
            default:
                return R.drawable.glow_border;
        }
    }
    
    public int getButtonSelector() {
        switch (currentTheme) {
            case THEME_DARK:
                return R.drawable.dark_button_selector;
            case THEME_NEON:
                return R.drawable.neon_button_selector;
            case THEME_MINIMALIST:
                return R.drawable.minimalist_button_selector;
            default:
                return R.drawable.button_selector;
        }
    }
    
    public int getBackgroundColor() {
        switch (currentTheme) {
            case THEME_DARK:
                return R.color.dark_bg_primary;
            case THEME_NEON:
                return R.color.neon_bg_primary;
            case THEME_MINIMALIST:
                return R.color.minimalist_bg_primary;
            default:
                return R.color.background_dark;
        }
    }
    
    public int getTextPrimaryColor() {
        switch (currentTheme) {
            case THEME_DARK:
                return R.color.dark_text_primary;
            case THEME_NEON:
                return R.color.neon_text_primary;
            case THEME_MINIMALIST:
                return R.color.minimalist_text_primary;
            default:
                return R.color.text_primary;
        }
    }
    
    public int getTextSecondaryColor() {
        switch (currentTheme) {
            case THEME_DARK:
                return R.color.dark_text_secondary;
            case THEME_NEON:
                return R.color.neon_text_secondary;
            case THEME_MINIMALIST:
                return R.color.minimalist_text_secondary;
            default:
                return R.color.text_secondary;
        }
    }
    
    public int getAccentColor() {
        switch (currentTheme) {
            case THEME_DARK:
                return R.color.dark_accent;
            case THEME_NEON:
                return R.color.neon_accent;
            case THEME_MINIMALIST:
                return R.color.minimalist_accent;
            default:
                return R.color.colorAccent;
        }
    }
    
    public String getThemeName() {
        switch (currentTheme) {
            case THEME_DARK:
                return "Dark";
            case THEME_NEON:
                return "Neon";
            case THEME_MINIMALIST:
                return "Minimalist";
            default:
                return "Default";
        }
    }
    
    public void applyThemeToView(View view) {
        if (view != null) {
            if (view instanceof android.widget.Button) {
                view.setBackgroundResource(getButtonSelector());
            } else {
                view.setBackgroundResource(getGlowBorderDrawable());
            }
        }
    }
    
    public void applyThemeToTextView(TextView textView, boolean isPrimary) {
        if (textView != null) {
            if (isPrimary) {
                textView.setTextColor(context.getResources().getColor(getTextPrimaryColor()));
            } else {
                textView.setTextColor(context.getResources().getColor(getTextSecondaryColor()));
            }
        }
    }
} 